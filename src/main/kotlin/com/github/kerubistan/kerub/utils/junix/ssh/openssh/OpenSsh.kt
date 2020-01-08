package com.github.kerubistan.kerub.utils.junix.ssh.openssh

import com.github.kerubistan.kerub.host.appendToFile
import com.github.kerubistan.kerub.host.checkFileExists
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.getFileContents
import com.github.kerubistan.kerub.utils.getLogger
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.apache.sshd.common.subsystem.sftp.SftpConstants
import java.math.BigInteger

/**
 * junix utility wrapper to manipulate the configuration files of openssh client and server
 */
object OpenSsh {
	private val logger = getLogger(OpenSsh::class)
	private const val knownHosts = ".ssh/known_hosts"
	private const val authorizedKeys = ".ssh/authorized_keys"

	private val ddOutputRecordsLineFormat = "\\d+\\+\\d+ records out".toRegex()
	private val ddInputRecordsLineFormat = "\\d+\\+\\d+ records in".toRegex()

	private val copyBlockDeviceErrorMatcher = mapOf(
			0 to ddInputRecordsLineFormat,
			1 to ddOutputRecordsLineFormat,
			3 to ddInputRecordsLineFormat,
			4 to ddOutputRecordsLineFormat
	)

	fun verifySshConnection(session: ClientSession, targetAddress: String) {
		session.executeOrDie("""bash -c "ssh -o BatchMode=true $targetAddress echo connected" """)
	}

	fun keyGen(session: ClientSession, password: String? = null): String =
			session.createSftpClient().use {
				checkSShDir(it, session)
				if (it.checkFileExists("/root/.ssh/id_rsa.pub")) {
					logger.warn("The file already exists, skipping generation")
				} else {
					session.executeOrDie(
							"ssh-keygen -t rsa -N '${password ?: ""}' -f /root/.ssh/id_rsa ")
				}
				it.getFileContents("/root/.ssh/id_rsa.pub")
			}

	fun authorize(session: ClientSession, pubkey: String) {
		//synchronized to make sure multiple threads do not overwrite each other's results
		synchronized(session) {
			session.createSftpClient().use {
				checkSShDir(it, session)
				logger.debug("{}: installing public key", session)
				it.appendToFile(authorizedKeys, "\n$pubkey")
				val stat = it.stat(authorizedKeys)
				logger.debug("{}: setting permissions", session)
				it.setStat(authorizedKeys, stat.perms(SftpConstants.S_IRUSR or SftpConstants.S_IWUSR))
			}
		}
	}

	fun unauthorize(session: ClientSession, pubkey: String) {
		synchronized(session) {
			session.createSftpClient().use {
				val filtered = it.read(authorizedKeys).reader(Charsets.US_ASCII).readLines()
						.filterNot { it.contains(pubkey) }.joinToString("\n")
				it.write(authorizedKeys).writer(Charsets.US_ASCII).write(filtered)
			}
		}
	}

	private fun checkSShDir(it: SftpClient, session: ClientSession) {
		if (!it.checkFileExists(".ssh")) {
			logger.debug("{}: creating .ssh directory", session)
			it.mkdir(".ssh")
		}
	}

	fun addKnownHost(session: ClientSession, hostAddress: String, pubKey: String) {
		synchronized(session) {
			session.createSftpClient().use {
				checkSShDir(it, session)
				it.appendToFile(
						knownHosts,
						"$hostAddress $pubKey\n"
				)
				val stat = it.stat(knownHosts)
				it.setStat(knownHosts, stat.perms(SftpConstants.S_IRUSR or SftpConstants.S_IWUSR))
			}
		}
	}

	fun removeKnownHost(session: ClientSession, hostAddress: String) {
		synchronized(session) {
			session.createSftpClient().use {
				val filtered = it.read(authorizedKeys).reader(Charsets.US_ASCII).readLines()
						.filterNot { it.startsWith("$hostAddress ssh-rsa") }.joinToString("\n")
				it.write(authorizedKeys).writer(Charsets.US_ASCII).write(filtered)
			}
		}
	}

	private val String?.pipeIn get() = if (this == null) "" else "| $this"

	private val String?.pipeOut get() = if (this == null) "" else "$this |"

	private fun Any?.flag(paramName: String) = if (this == null) {
		""
	} else {
		"$paramName=$this"
	}

	fun copyBlockDevice(
			session: ClientSession,
			sourceDevice: String,
			targetAddress: String,
			targetDevice: String,
			filters: Pair<String, String>? = null,
			bytes: BigInteger? = null
	) {
		session.executeOrDie(
				"""
					bash -c "
					dd if=$sourceDevice ${bytes.flag("count")} ${filters?.first.pipeIn} 
					${if (bytes != null) "iflag=count_bytes" else ""}
					| ssh -o BatchMode=true $targetAddress ${filters?.second.pipeOut} dd of=$targetDevice
					" """.trimIndent().replace("\n", ""),
				isError = {
					it.isNotBlank() &&
							it.trim().lines().filter(String::isNotBlank).let { lines ->
								lines.size != 6
								!copyBlockDeviceErrorMatcher.all { (lineNr, regexp) ->
									lines[lineNr].matches(regexp)
								}
							}
				}
		)
	}

}
