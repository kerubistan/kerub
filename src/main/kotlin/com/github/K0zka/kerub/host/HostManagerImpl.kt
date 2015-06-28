package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.distros.Distribution
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.services.HostAndPassword
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.vmstat.VmStat
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.ServerKeyVerifier
import java.net.SocketAddress
import java.security.KeyPair
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
		val hostAssigner: ControllerAssigner) : HostManager {

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
		//TODO: start monitoring processes
	}

	override fun join(host: Host, password : String): Host {
		val session = sshClientService.loginWithPassword(
				address = host.address,
				userName = "root",
				password =password)
		sshClientService.installPublicKey(session)
		val capabilities = discoverer.discoverHost(session)

		val host = host.copy(capabilities = capabilities)

		hostDao.add(host)
		val controllerId = hostAssigner.assignController(host)
		hostAssignmentDao.add(Assignment(controller = controllerId, hostId = host.id))

		return host
	}


	fun start() {
		logger.info("starting host manager")
		hostAssignmentDao.listByController(controllerManager.getControllerId()).forEach {
			logger.info("connecting assigned host {}", it.hostId)
			val host = hostDao.get(it.hostId)
			if (host != null) {
				connectHost(host)
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