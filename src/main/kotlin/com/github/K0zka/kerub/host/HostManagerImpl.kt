package com.github.K0zka.kerub.host

import org.apache.sshd.SshClient
import org.slf4j.LoggerFactory
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.KeyExchange
import org.apache.sshd.ClientSession
import java.net.SocketAddress
import java.security.PublicKey
import org.apache.sshd.client.ServerKeyVerifier
import org.apache.sshd.server.PublickeyAuthenticator
import org.apache.sshd.server.session.ServerSession
import java.security.KeyPair
import org.apache.sshd.common.SessionListener
import org.apache.sshd.common.Session

public class HostManagerImpl : HostManager {

	class object val logger = LoggerFactory.getLogger(javaClass<HostManagerImpl>())!!

	class ServerKeyReader : ServerKeyVerifier {
		override fun verifyServerKey(sshClientSession: ClientSession?, remoteAddress: SocketAddress?, serverKey: PublicKey?): Boolean {
			logger.info("server key : {}", serverKey)
			this.serverKey = serverKey
			return true
		}
		class object val logger = LoggerFactory.getLogger(javaClass<ServerKeyReader>())!!

		var serverKey : PublicKey? = null
	}

	override fun getHostPublicKey(address: String): PublicKey {
		val sshClient = SshClient.setUpDefaultClient()!!
		val serverKeyReader = ServerKeyReader()
		sshClient.setServerKeyVerifier( serverKeyReader )
		sshClient.start()
		try {
			val connect = sshClient.connect("root", address, 22)!!

			connect.await()
			val session = connect.getSession()!!
			session.auth()!!.await()
			logger.info(session.getServerVersion())
			return serverKeyReader.serverKey!!
		} finally {
			sshClient.stop()
		}

	}
	override fun registerHost() {
		throw UnsupportedOperationException()
	}
}