package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.utils.getLogger
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.ClientBuilder
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.AbstractClientChannel
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.apache.sshd.common.config.keys.KeyUtils
import org.apache.sshd.common.digest.BuiltinDigests
import org.apache.sshd.common.digest.Digest
import org.apache.sshd.common.session.Session
import org.apache.sshd.common.signature.BuiltinSignatures
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.util.EnumSet

private val logger = getLogger(ClientSession::class)

const val sshPort = 22

private fun <T> Logger.debugAndReturn(msg: String, x: T): T {
	if (this.isDebugEnabled) {
		this.debug("$msg $x")
	}
	return x
}


fun <T> AbstractClientChannel.use(fn: (AbstractClientChannel) -> T): T {
	try {
		this.open().await(GLOBAL_SSH_TIMEOUT_MS)
		return fn(this)
	} finally {
		this.close(true)
	}
}

fun ClientSession.execute(command: String): String {
	return this.createExecChannel(command).use {
		it.invertedOut.reader(Charset.forName("ASCII")).use { inputStream ->
			logger.debugAndReturn("result of command $command: ", inputStream.readText())
		}
	}
}

/**
 * Executes a command and returns the result or throws exception, any
 * output on stderr is considered error.
 */
fun ClientSession.executeOrDie(command: String): String =
	this.executeOrDie(command, String::isNotBlank)

class StdErrLoggingOutputStream(private val session : ClientSession) : OutputStream() {

	private val buff = StringBuilder()

	override fun write(data: Int) {
		if(data.toChar() == '\n') {
			logger.warn("${session.connectAddress}/stderr: $buff")
			buff.clear()
		} else {
			buff.append(data.toChar())
		}

	}

}

const val GLOBAL_SSH_TIMEOUT_MS = 10_000.toLong()

/**
 * Starts a process that runs on the output.
 */
fun ClientSession.process(command: String, output : OutputStream) {
	val exec = this.createExecChannel(command)
	exec.`in` = NullInputStream(0)
	exec.err = StdErrLoggingOutputStream(this)
	exec.out = output
	exec.open().verify(GLOBAL_SSH_TIMEOUT_MS)
}

fun ClientSession.bashMonitor(command: String, interval: Int, separator : String, output: OutputStream)
		= this.process("""bash -c "while true; do $command; echo $separator; sleep $interval; done;" """, output)

fun ClientSession.executeOrDie(command: String, isError: (String) -> Boolean, cs: Charset = charset("ASCII")): String {
	val execChannel = this.createExecChannel(command)
	logger.debug("executing command on host {}: {}",this.connectAddress, command)
	return execChannel.use {
		it.invertedErr?.reader(cs)?.readText()?.let {
			error ->
			if (isError(error)) {
				throw IOException(error)
			} else if (error.isNotBlank()) {
				logger.info("Error output ignored by command {} : {}", command, error)
			}
		}
		it.invertedOut.reader(cs).use {
			logger.debugAndReturn("result of command $command: ", it.readText())
		}
	}
}


/**
 * Check if a file exists.
 * Sftp channel is created, only use this if there is no sftp channel open yet.
 */
fun ClientSession.checkFileExists(file: String): Boolean {
	return this.createSftpClient().use {
		it.checkFileExists(file)
	}
}

/**
 * Check if a file exists.
 */
fun SftpClient.checkFileExists(file: String): Boolean {
	return try {
		this.stat(file)
		true
	} catch (e: IOException) {
		//this is fine, it happens when the file does not exist
		false
	}
}

fun ClientSession.appendToFile(file: String, content: String) {
	this.createSftpClient().use {
		it.appendToFile(file, content)
	}
}

fun SftpClient.appendToFile(file: String, content: String) {
	val handle = this.open(file, EnumSet.of<SftpClient.OpenMode>(
			SftpClient.OpenMode.Append,
			SftpClient.OpenMode.Create,
			SftpClient.OpenMode.Write))
	try {
		val contentBytes = content.toByteArray(charset("ASCII"))
		val stat = this.stat(handle)
		this.write(handle, stat.size, contentBytes, 0, contentBytes.size)
	} finally {
		this.close(handle)
	}
}

/**
 * Get the contents of a file.
 * This should be used only on small files, usually configuration files.
 * Opens a session and closes it afterwards.
 */
fun ClientSession.getFileContents(file: String) = this.createSftpClient().use { it.getFileContents(file) }

/**
 * Get the contents of a file.
 * This should be used only on small files, usually configuration files.
 */
fun SftpClient.getFileContents(file: String): String = this.read(file).use {
		logger.debugAndReturn("Contents of file $file: ", it.reader(Charsets.US_ASCII).readText())
	}

inline fun <T, S : Session> S.use(action: (S) -> T): T {
	try {
		return action(this)
	} finally {
		close(true)
	}
}

val digest: Digest = BuiltinDigests.md5.create()

/**
 *
 */
fun getSshFingerPrint(key: PublicKey) = KeyUtils.getFingerPrint(digest, key).substringAfter("MD5:")

fun encodePublicKey(key: PublicKey): String {
	val out = ByteArrayOutputStream()

	when(key) {
		is RSAPublicKey -> {
			out.write(encodeString("ssh-rsa"))
			out.write(encodeByteArray(key.publicExponent.toByteArray()))
			out.write(encodeByteArray(key.modulus.toByteArray()))
		}
		is ECPublicKey -> {
			out.write(encodeString("ecdsa-sha2-nistp256"))
			out.write(encodeByteArray(key.w.affineX.toByteArray()))
			out.write(encodeByteArray(key.w.affineY.toByteArray()))
		}

	}

	return out.toByteArray().toBase64()
}

fun encodeString(str: String): ByteArray {
	val bytes = str.toByteArray(charset("ASCII"))
	return encodeByteArray(bytes)
}

private fun encodeByteArray(bytes: ByteArray): ByteArray {
	val out = encodeUInt32(bytes.size).copyOf(bytes.size + 4)
	bytes.forEachIndexed { idx, byte -> out[idx + 4] = byte }
	return out
}

private fun encodeUInt32(value: Int): ByteArray {
	val bytes = ByteArray(4)
	bytes[0] = value.shr(24).and(0xff).toByte()
	bytes[1] = value.shr(16).and(0xff).toByte()
	bytes[2] = value.shr(8).and(0xff).toByte()
	bytes[3] = value.and(0xff).toByte()
	return bytes
}

fun createSshClient() =
		ClientBuilder.builder()
				.signatureFactories(listOf(BuiltinSignatures.rsa))
				.build() as SshClient
