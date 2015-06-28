package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.SftpClient
import java.io.ByteArrayOutputStream
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
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
"""
#added by kerub - ${Date()}
ssh-rsa ${encodePublicKey(keyPair.getPublic() as RSAPublicKey)}
""")
			val stat = it.stat(".ssh/authorized_keys")
			logger.debug("{}: setting permissions", session)
			it.setStat(".ssh/authorized_keys", stat.perms(SftpClient.S_IRUSR or SftpClient.S_IWUSR))
		}
		logger.debug("{}: public key installation finished", session)
	}

	fun encodePublicKey(key : RSAPublicKey) : String {
		val out = ByteArrayOutputStream()

		out.write(encodeString("ssh-rsa"))
		out.write(encodeByteArray(key.getPublicExponent().toByteArray()))
		out.write(encodeByteArray(key.getModulus().toByteArray()))

		return out.toByteArray().toBase64()
	}

	fun encodeString(str : String) : ByteArray {
		val bytes = str.toByteArray("ASCII")
		return encodeByteArray(bytes)
	}

	private fun encodeByteArray(bytes: ByteArray): ByteArray {
		val out = encodeUInt32(bytes.size()).copyOf(bytes.size() + 4)
		bytes.forEachIndexed { idx, byte -> out[idx + 4] = byte }
		return out
	}

	fun encodeUInt32(value : Int) : ByteArray {
		val bytes = ByteArray(4)
		bytes[0] = value.shr(24).and(0xff).toByte()
		bytes[1] = value.shr(16).and(0xff).toByte()
		bytes[2] = value.shr(8).and(0xff).toByte()
		bytes[3] = value.and(0xff).toByte()
		return bytes
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
		val authFuture = session.auth()
		val finished = authFuture.await( maxWait, maxWaitUnit )
		logger.debug("{}: Authentication finished: {} success: {}", address, finished, authFuture.isSuccess())
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