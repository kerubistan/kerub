package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.utils.DefaultSshEventListener
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.SshException
import org.apache.sshd.common.session.Session
import org.apache.sshd.common.session.SessionListener
import org.apache.sshd.common.session.helpers.AbstractSession
import java.security.KeyPair
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SshClientServiceImpl(
		val client: SshClient = createSshClient(),
		val keyPair: KeyPair,
		val maxWait: Long = 500,
		val maxWaitUnit: TimeUnit = TimeUnit.MILLISECONDS
) : SshClientService {

	class ServerFingerprintChecker(val expected: String) : DefaultSshEventListener() {
		override fun sessionEvent(session: Session, event: SessionListener.Event) {
			if (SessionListener.Event.KeyEstablished == event) {
				checkServerFingerPrint(session, expected)
			}
		}
	}

	companion object {
		private val logger = getLogger()

		fun checkServerFingerPrint(session: Session, expected: String) {
			val serverKey = (session as AbstractSession).kex.serverKey
			val fingerprint = getSshFingerPrint(serverKey)
			logger.debug("checking server ssh fingerprint {}", serverKey)
			if (fingerprint != expected) {
				throw SshException("Ssh key $fingerprint does not match expected $expected ")
			}
		}
	}


	override fun installPublicKey(session: ClientSession) {
		logger.debug("{}: installing kerub public key in ssh session", session)
		session.createSftpClient().use {
			OpenSsh.authorize(session, getPublicKey())
		}
		logger.debug("{}: public key installation finished", session)
	}

	override fun createSession(address: String, userName: String): ClientSession {
		val future = client.connect(userName, address, sshPort)
		future.await()
		return future.session
	}

	override fun loginWithPublicKey(address: String, userName: String, hostPublicKey: String): ClientSession {
		logger.debug("connecting to {} with public key", address)
		val session = createVerifiedSession(address, userName, hostPublicKey)
		logger.debug("sending key to {}", address)
		session.addPublicKeyIdentity(keyPair)
		return checkLogin(address, session)
	}

	private fun checkLogin(address: String, session: ClientSession): ClientSession {
		logger.debug("waiting for authentication from {}", address)
		val authFuture = session.auth()
		val finished = authFuture.await(maxWait, maxWaitUnit)
		authFuture.verify()
		logger.debug("{}: Authentication finished: {} success: {}", address, finished, authFuture.isSuccess)
		return session
	}

	override fun loginWithPassword(
			address: String, userName: String, password: String, hostPublicKey: String
	): ClientSession {
		logger.debug("connecting to {} with password", address)
		val session = createVerifiedSession(address, userName, hostPublicKey)
		logger.debug("sending password {}", address)
		session.addPasswordIdentity(password)
		return checkLogin(address, session)
	}

	private fun createVerifiedSession(address: String, userName: String, hostPublicKey: String): ClientSession =
			createSession(address, userName).apply { addSessionListener(ServerFingerprintChecker(hostPublicKey)) }

	override fun getPublicKey(): String = "ssh-rsa ${encodePublicKey(keyPair.public as RSAPublicKey)}"

	override fun getHostPublicKey(addr: String): PublicKey = client.connect("test", addr, sshPort).let { future ->
		if (future.await(maxWait, maxWaitUnit)) {
			val state = future.session.waitFor(listOf(ClientSession.ClientSessionEvent.WAIT_AUTH), maxWait)
			if (ClientSession.ClientSessionEvent.WAIT_AUTH in state) {
				future.session.kex.serverKey
			} else {
				throw SecurityException("Session communication timed out: $state")
			}
		} else {
			throw TimeoutException("Could not open session in $maxWait $maxWaitUnit")
		}
	}

}
