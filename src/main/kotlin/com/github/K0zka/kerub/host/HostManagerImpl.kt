package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.ServerKeyVerifier
import java.net.SocketAddress
import java.security.PublicKey
import java.util.Collections
import java.util.UUID

public class HostManagerImpl (
		val hostDao : HostDao,
		val hostDynamicDao : HostDynamicDao,
		val sshClientService : SshClientService,
		val controllerManager : ControllerManager,
		val hostAssignmentDao : AssignmentDao,
		val discoverer: HostCapabilitiesDiscoverer,
		val hostAssigner: ControllerAssigner) : HostManager, HostCommandExecutor {

	override fun execute(host: Host, closure: (ClientSession) -> Unit) {
		val session = connections[host.id]
		if(session != null) {
			closure(session)
		}
	}

	companion object {
		val logger = getLogger(HostManagerImpl::class)
		val defaultSshServerPort = 22
		val defaultSshUserName = "root"
	}

	public var sshServerPort : Int = defaultSshServerPort
	public var sshUserName : String = defaultSshUserName
	private val connections = Collections.synchronizedMap(hashMapOf<UUID, ClientSession>())

	override fun connectHost(host: Host) {
		logger.info("Connecting to host {} {}", host.id, host.address)
		val session = sshClientService.loginWithPublicKey(host.address)
		connections.put(host.id, session)

		val distro = discoverer.detectDistro(session)
		distro?.startMonitorProcesses(session, host, hostDynamicDao)
	}

	override fun join(host: Host, password: String): Host {
		val session = sshClientService.loginWithPassword(
				address = host.address,
				userName = "root",
				password = password)
		sshClientService.installPublicKey(session)

		return joinConnectedHost(host, session)
	}

	override fun join(host: Host): Host {
		val session = sshClientService.loginWithPublicKey(
				address = host.address,
				userName = "root"
		                                                 )
		return joinConnectedHost(host, session)
	}

	internal fun joinConnectedHost(host: Host, session: ClientSession): Host {
		val capabilities = discoverer.discoverHost(session)

		val host = host.copy(capabilities = capabilities)

		hostDao.add(host)
		hostAssigner.assignController(host)

		return host
	}

	fun start() {
		logger.info("starting host manager")
		hostAssignmentDao.listByController(controllerManager.getControllerId()).forEach {
			logger.info("connecting assigned host {}", it.hostId)
			val host = hostDao.get(it.hostId)
			if (host != null) {
				//TODO: this try-catch should be temporary, refactor to a threadpool
				//anyway it would be a bad idea to wait for 100+ hosts to be connected
				//over a slow, unreliable network
				try {
					connectHost(host)
				} catch (e : Exception) {
					logger.error("Could not connect host {} at {}",host.id, host.address, e)
				}
			} else {
				logger.warn("Host {} assigned to {} but not found in host records, removing assignment", it.hostId, it.controller)
				hostAssignmentDao.remove(it)
			}
		}
	}

	fun stop() {
		logger.info("stopping host manager")
		//TODO: disconnect hosts quick but nice
	}

	class ServerKeyReader : ServerKeyVerifier {
		override fun verifyServerKey(sshClientSession: ClientSession?, remoteAddress: SocketAddress?, serverKey: PublicKey?): Boolean {
			logger.debug("server key : {}", serverKey)
			this.serverKey = serverKey
			return true
		}
		companion object val logger = getLogger(ServerKeyReader::class)

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