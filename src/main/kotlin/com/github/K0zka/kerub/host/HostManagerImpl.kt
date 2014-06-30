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
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.utils.getLogger
import java.util.Collections
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao

public class HostManagerImpl (val keyPair : KeyPair, val hostDao : HostDao, val hostDynamicDao : HostDynamicDao) : HostManager {

	class object {
		val logger = getLogger(javaClass<HostManagerImpl>())
		val defaultSshServerPort = 22
		val defaultSshUserName = "root"
	}

	public var sshServerPort : Int = defaultSshServerPort
	public var sshUserName : String = defaultSshUserName
	protected val sshClient : SshClient = SshClient.setUpDefaultClient()
	private val connections = Collections.synchronizedMap(mapOf<String, ClientSession>())

	fun start() {
		sshClient.start()
	}

	fun stop() {
		sshClient.stop()
	}

	override fun connectHost(host: Host) {
		sshClient.connect(sshUserName, host.address, sshServerPort).addListener {
			logger.info("host connected")
			connections.put(host.address, it.getSession())

		}
	}

	fun executeCommand() {

	}

	class ServerKeyReader : ServerKeyVerifier {
		override fun verifyServerKey(sshClientSession: ClientSession?, remoteAddress: SocketAddress?, serverKey: PublicKey?): Boolean {
			logger.debug("server key : {}", serverKey)
			this.serverKey = serverKey
			return true
		}
		class object val logger = getLogger(javaClass<ServerKeyReader>())

		var serverKey : PublicKey? = null
	}

	override fun getHostPublicKey(address: String): PublicKey {
		val pubKeySshClient = SshClient.setUpDefaultClient()!!
		val serverKeyReader = ServerKeyReader()
		pubKeySshClient.setServerKeyVerifier( serverKeyReader )
		pubKeySshClient.start()
		try {
			val connect = pubKeySshClient.connect(sshUserName, address, sshServerPort)!!

			connect.await()
			val session = connect.getSession()!!
			session.auth()!!.await()
			logger.info(session.getServerVersion())
			return serverKeyReader.serverKey!!
		} finally {
			pubKeySshClient.stop()
		}

	}
	override fun registerHost() {
		throw UnsupportedOperationException()
	}
}