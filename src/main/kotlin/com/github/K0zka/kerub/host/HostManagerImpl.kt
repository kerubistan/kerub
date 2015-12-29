package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.distros.Distribution
import com.github.K0zka.kerub.host.lom.WakeOnLan
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.hypervisor.kvm.KvmHypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.controller.AssignmentType
import com.github.K0zka.kerub.utils.DefaultSshEventListener
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.ServerKeyVerifier
import org.apache.sshd.common.Session
import java.net.SocketAddress
import java.security.PublicKey
import java.util.Collections
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

public class HostManagerImpl (
		val hostDao : HostDao,
		val hostDynamicDao : HostDynamicDao,
		val sshClientService : SshClientService,
		val controllerManager : ControllerManager,
		val hostAssignmentDao : AssignmentDao,
		val discoverer: HostCapabilitiesDiscoverer,
		val hostAssigner: ControllerAssigner) : HostManager, HostCommandExecutor {

	val timer = Timer("host-manager")

	class ReconnectDisconnectedHosts(private val hostManager : HostManagerImpl) : TimerTask() {
		override fun run() {
			hostManager.connectHosts()
		}

	}

	override fun disconnectHost(host: Host) {
		val session = connections.remove(host.id)
		session?.first?.close(true)
	}

	override fun getHypervisor(host: Host): Hypervisor? {
		val connection = connections[host.id]
		if(connection != null) {
			return KvmHypervisor(connection.first)
		} else {
			return null;
		}
	}

	override fun getPowerManager(host: Host): PowerManager {
		require(host.dedicated, {"If host is not dedicated, it can not be power-managed"})
		//TODO
		return WakeOnLan(host, this, this)
	}

	override fun execute(host: Host, closure: (ClientSession) -> Unit) {
		val session = connections[host.id]
		if(session != null) {
			closure(session.first)
		}
	}

	companion object {
		val logger = getLogger(HostManagerImpl::class)
		val defaultSshServerPort = 22
		val defaultSshUserName = "root"
	}

	class SessionCloseListener(
			private val host : Host,
			private val hostDynamicDao : HostDynamicDao,
			private val connections : MutableMap<UUID, Pair<ClientSession, Distribution>>
	) : DefaultSshEventListener() {
		override fun sessionClosed(session: Session) {
			logger.info("Session closed for host:\n addrs: {}\n id: {}", host.address, host.id)
			// things to do here, this host is dead for some reason, to register that, the dynamic record is removed
			// this also tells the planner to act on the event
			hostDynamicDao.remove(host.id)
			//clean up: remove the host connection
			connections.remove(host.id)
		}
	}

	public var sshServerPort : Int = defaultSshServerPort
	public var sshUserName : String = defaultSshUserName
	private val connections = Collections.synchronizedMap(hashMapOf<UUID, Pair<ClientSession, Distribution>>())

	override fun connectHost(host: Host) {
		logger.info("Connecting to host {} {}", host.id, host.address)
		val session = sshClientService.loginWithPublicKey(
				address = host.address,
				hostPublicKey = host.publicKey)
		session.addListener(SessionCloseListener(host, hostDynamicDao, connections))
		val distro = discoverer.detectDistro(session)
		if(distro != null) {
			connections.put(host.id, session to distro)
			distro.startMonitorProcesses(session, host, hostDynamicDao)
		}
	}

	override fun join(host: Host, password: String): Host {
		val session = sshClientService.loginWithPassword(
				address = host.address,
				userName = "root",
				password = password,
				hostPublicKey = host.publicKey)
		sshClientService.installPublicKey(session)

		return joinConnectedHost(host, session)
	}

	override fun join(host: Host): Host {
		val session = sshClientService.loginWithPublicKey(
				address = host.address,
				userName = "root",
				hostPublicKey = host.publicKey
		                                                 )
		return joinConnectedHost(host, session)
	}

	internal fun joinConnectedHost(host: Host, session: ClientSession): Host {
		val capabilities = discoverer.discoverHost(session)

		val internalHost = host.copy(capabilities = capabilities)

		hostDao.add(internalHost)
		hostAssigner.assignController(host)

		return host
	}

	private fun connectHosts() {
		hostAssignmentDao.listByControllerAndType(controllerManager.getControllerId(), AssignmentType.host).filterNot {
			connections.containsKey(it.entityId)
		}.forEach {
			logger.info("connecting assigned host {}", it.entityId)
			val host = hostDao.get(it.entityId)
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
				logger.warn("Host {} assigned to {} but not found in host records, removing assignment", it.entityId, it.controller)
				hostAssignmentDao.remove(it)
			}
		}
	}

	fun start() {
		logger.info("connecting to assigned hosts")
		connectHosts()
		timer.schedule(ReconnectDisconnectedHosts(this), 60000L, 60000L)
	}

	fun stop() {
		logger.info("stopping host manager")
		timer.cancel()
		//TODO: disconnect hosts quick but nice
	}

	class ServerKeyReader : ServerKeyVerifier {
		override fun verifyServerKey(sshClientSession: ClientSession?, remoteAddress: SocketAddress?, serverKey: PublicKey?): Boolean {
			logger.debug("server key : {}", serverKey)
			this.serverKey = serverKey
			return true
		}
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