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
import java.io.ByteArrayOutputStream
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.concurrent.TimeUnit

class SshClientServiceImpl(
		val client: SshClient = SshClient.setUpDefaultClient(),
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
		private val logger = getLogger(SshClientServiceImpl::class)

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

	fun encodePublicKey(key: RSAPublicKey): String {
		val out = ByteArrayOutputStream()

		out.write(encodeString("ssh-rsa"))
		out.write(encodeByteArray(key.publicExponent.toByteArray()))
		out.write(encodeByteArray(key.modulus.toByteArray()))

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

	fun encodeUInt32(value: Int): ByteArray {
		val bytes = ByteArray(4)
		bytes[0] = value.shr(24).and(0xff).toByte()
		bytes[1] = value.shr(16).and(0xff).toByte()
		bytes[2] = value.shr(8).and(0xff).toByte()
		bytes[3] = value.and(0xff).toByte()
		return bytes
	}

	override fun createSession(address: String, userName: String): ClientSession {
		val future = client.connect(userName, address, 22)
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

	override fun loginWithPassword(address: String, userName: String, password: String, hostPublicKey: String): ClientSession {
		logger.debug("connecting to {} with password", address)
		val session = createVerifiedSession(address, userName, hostPublicKey)
		logger.debug("sending password {}", address)
		session.addPasswordIdentity(password)
		return checkLogin(address, session)
	}

	private fun createVerifiedSession(address: String, userName: String, hostPublicKey: String): ClientSession =
			createSession(address, userName).apply { addSessionListener(ServerFingerprintChecker(hostPublicKey)) }

	override fun getPublicKey(): String = """
ssh-rsa ${encodePublicKey(keyPair.public as RSAPublicKey)} #added by kerub - ${Date()}
"""

}