package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.client.channel.AbstractClientChannel
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.apache.sshd.common.config.keys.KeyUtils
import org.apache.sshd.common.digest.BuiltinDigests
import org.apache.sshd.common.digest.Digest
import org.apache.sshd.common.session.Session
import org.slf4j.Logger
import java.io.IOException
import java.nio.charset.Charset
import java.security.PublicKey
import java.util.EnumSet

private val logger = getLogger(ClientSession::class)

private fun <T> Logger.debugAndReturn(msg: String, x: T): T {
	if (this.isDebugEnabled) {
		this.debug("$msg $x")
	}
	return x
}


fun <T> AbstractClientChannel.use(fn: (AbstractClientChannel) -> T): T {
	try {
		this.open().await()
		return fn(this)
	} finally {
		logger.debug("closing client channel")
		this.close(true)
	}
}

fun ClientSession.execute(command: String): String {
	return this.createExecChannel(command).use {
		it.invertedOut.reader(Charset.forName("ASCII")).use {
			logger.debugAndReturn("result of command $command: ", it.readText())
		}
	}
}

fun ClientSession.executeOrDie(command: String): String {
	return this.executeOrDie(command, { it.isNotBlank() })
}

fun ClientSession.executeOrDie(command: String, isError: (String) -> Boolean, cs: Charset = charset("ASCII")): String {
	val execChannel = this.createExecChannel(command)
	logger.debug("executing command: {}", command)
	return execChannel.use {
		val error = it.invertedErr.reader(cs).readText()
		if (isError(error)) {
			throw IOException(error)
		} else if (error.isNotBlank()) {
			logger.warn("Error output ignored by command {} : {}", command, error)
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
	try {
		this.stat(file)
		return true
	} catch (e: IOException) {
		//this is fine, it happens when the file does not exist
		return false
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
fun ClientSession.getFileContents(file: String): String {
	return this.createSftpClient().use {
		it.getFileContents(file)
	}
}

/**
 * Get the contents of a file.
 * This should be used only on small files, usually configuration files.
 */
fun SftpClient.getFileContents(file: String): String {
	return this.read(file).use {
		logger.debugAndReturn("Contents of file $file: ", it.reader(Charsets.US_ASCII).readText())
	}
}

fun <T, S : Session> S.use(action: (S) -> T): T {
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