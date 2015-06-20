package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.SftpClient
import java.security.KeyPair
import java.util.Date
import java.util.concurrent.TimeUnit

public class SshClientServiceImpl(
		val client: SshClient,
		val keyPair: KeyPair,
		val maxWait : Long = 500,
		val maxWaitUnit : TimeUnit = TimeUnit.MILLISECONDS) : SshClientService {

	companion object {
		val logger = getLogger(SshClientServiceImpl::class)
	}

	override fun installPublicKey(session: ClientSession) {
		logger.debug("{}: installing kerub public key in ssh session", session)
		session.createSftpClient().use {
			if (!it.checkFileExists(".ssh")) {
				logger.debug("{}: creating .ssh directory", session)
				it.mkdir(".ssh")
			}
			logger.debug("{}: installing public key", session)
			it.appendToFile(".ssh/authorized_keys",
""""
#added by kerub - ${Date()}
ssh-rsa ${keyPair.getPublic().getEncoded().toBase64()}
""")
			val stat = it.stat(".ssh/authorized_keys")
			logger.debug("{}: setting permissions", session)
			it.setStat(".ssh/authorized_keys", stat.perms(SftpClient.S_IRUSR or SftpClient.S_IWUSR))
		}
		logger.debug("{}: public key installation finished", session)
	}

	override fun createSession(address: String, userName: String): ClientSession {
		return client.connect(userName, address, 22).await().getSession()
	}

	override fun loginWithPublicKey(address: String, userName: String): ClientSession {
		logger.debug("connecting to {} with public key", address)
		val session = client.connect(userName, address, 22).await().getSession()
		logger.debug("sending key to {}", address)
		session.addPublicKeyIdentity(keyPair)
		logger.debug("waiting for authentication from {}", address)
		session.auth().await( maxWait, maxWaitUnit )
		logger.debug("{}: Authentication finished", address)
		return session
	}

	override fun loginWithPassword(address: String, userName: String, password: String): ClientSession {
		logger.debug("connecting to {} with password", address)
		val session = client.connect(userName, address, 22).await().getSession()
		logger.debug("sending password {}", address)
		session.addPasswordIdentity(password)
		logger.debug("authenticating {}", address)
		session.auth().await(maxWait, maxWaitUnit)
		logger.debug("authentication finished {}", address)
		return session
	}
}