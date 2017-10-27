package com.github.kerubistan.kerub.utils.junix.ssh.openssh

import com.github.kerubistan.kerub.host.appendToFile
import com.github.kerubistan.kerub.host.checkFileExists
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.getLogger
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.apache.sshd.common.subsystem.sftp.SftpConstants
import java.util.Date

/**
 * junix utility wrapper to manipulate the configuration files of openssh client and server
 */
object OpenSsh {
	private val logger = getLogger(OpenSsh::class)
	private val knownHosts = ".ssh/known_hosts"
	private val authorizedKeys = ".ssh/authorized_keys"

	fun keyGen(session: ClientSession, password: String? = null) {
		session.executeOrDie("ssh-keygen -t rsa -N ${password ?: ""}")
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
				it.appendToFile(knownHosts,
						"$hostAddress $pubKey #added by kerub ${Date()}\n"
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

}
