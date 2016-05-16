package com.github.K0zka.kerub.utils.junix.ssh.openssh

import com.github.K0zka.kerub.host.appendToFile
import com.github.K0zka.kerub.host.checkFileExists
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.subsystem.sftp.SftpConstants

object OpenSsh {

	private val logger = getLogger(OpenSsh::class)

	fun keyGen(session: ClientSession, password: String? = null) {
		session.executeOrDie("ssh-keygen -t rsa -N ${password ?: ""}")
	}

	fun authorize(session: ClientSession, pubkey: String) {
		session.createSftpClient().use {
			if (!it.checkFileExists(".ssh")) {
				logger.debug("{}: creating .ssh directory", session)
				it.mkdir(".ssh")
			}
			logger.debug("{}: installing public key", session)
			it.appendToFile(".ssh/authorized_keys", "\n" + pubkey)
			val stat = it.stat(".ssh/authorized_keys")
			logger.debug("{}: setting permissions", session)
			it.setStat(".ssh/authorized_keys", stat.perms(SftpConstants.S_IRUSR or SftpConstants.S_IWUSR))
		}
	}
}
