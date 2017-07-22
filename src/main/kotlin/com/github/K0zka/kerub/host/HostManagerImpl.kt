package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.exc.HostAddressException
import com.github.K0zka.kerub.host.distros.Distribution
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.hypervisor.kvm.KvmHypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.controller.AssignmentType
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.utils.DefaultSshEventListener
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.silent
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.keyverifier.ServerKeyVerifier
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.apache.sshd.common.session.Session
import java.io.InputStream
import java.net.InetAddress
import java.net.SocketAddress
import java.net.UnknownHostException
import java.security.PublicKey
import java.util.Collections
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

open class HostManagerImpl(
		private val hostDao: HostDao,
		private val hostDynamicDao: HostDynamicDao,
		private val hostCfgDao: HostConfigurationDao,
		private val vmDynamicDao: VirtualMachineDynamicDao,
		private val virtualStorageDao: VirtualStorageDeviceDao,
		private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao,
		private val sshClientService: SshClientService,
		private val controllerManager: ControllerManager,
		private val hostAssignmentDao: AssignmentDao,
		private val discoverer: HostCapabilitiesDiscoverer,
		private val hostAssigner: ControllerAssigner) : HostManager, HostCommandExecutor {

	val timer = Timer("host-manager")


	override fun powerDown(host: Host) {
		require(host.dedicated, { "Can not power off a non-dedicated host" })
		execute(host, {
			it.execute("poweroff")
		})
		disconnectHost(host)
	}

	override fun disconnectHost(host: Host) {
		val session = connections.remove(host.id)
		session?.first?.close(true)
	}

	override fun getHypervisor(host: Host): Hypervisor? {
		val connection = connections[host.id]
		if (connection != null) {
			return KvmHypervisor(connection.first, host, hostDao, hostCfgDao, hostDynamicDao, vmDynamicDao, virtualStorageDao, virtualStorageDynDao)
		} else {
			//TODO: not connected: throw exception?
			return null
		}
	}

	override fun getFireWall(host: Host): FireWall {
		val conn = requireNotNull(connections[host.id])
		return conn.second.getFireWall(conn.first)
	}

	override fun getServiceManager(host: Host): ServiceManager {
		val conn = requireNotNull(connections[host.id])
		return conn.second.getServiceManager(conn.first)
	}

	override fun <T> execute(host: Host, closure: (ClientSession) -> T): T {
		val session = requireNotNull(connections[host.id], { "Host no connected: ${host.id} ${host.address}" })
		return closure(session.first)
	}

	override fun <T> dataConnection(host: Host, action: (ClientSession) -> T): T {

		val controllConnection = connections[host.id]?.first
		if (controllConnection == null) {
			return sshClientService.loginWithPublicKey(
					address = host.address,
					userName = "root",
					hostPublicKey = host.publicKey).use {
				session ->
				action(session)
			}
		} else {
			return action(controllConnection)
		}
	}

	override fun readRemoteFile(host: Host, path: String): InputStream {
		val controllConnection = connections[host.id]?.first
		return if (controllConnection == null) {
			val session = sshClientService.loginWithPublicKey(
					address = host.address,
					userName = "root",
					hostPublicKey = host.publicKey)
			val sftp = session.createSftpClient()
			DataSessionInputStream(
					session = session,
					sftp = sftp,
					stream = sftp.read(path)
			)
		} else {
			val sftp = controllConnection.createSftpClient()
			ControlSessionInputStream(
					stream = sftp.read(path),
					sftp = sftp
			)
		}
	}

	companion object {
		val logger = getLogger(HostManagerImpl::class)
		val defaultSshServerPort = 22
		val defaultSshUserName = "root"

		class ReconnectDisconnectedHosts(private val hostManager: HostManagerImpl) : TimerTask() {
			override fun run() {
				hostManager.connectHosts()
			}
		}

		/**
		 * InputStream proxy that closes the sftp client only
		 * and can be used by controllers, that will leave the ssh
		 * session open.
		 */
		open class ControlSessionInputStream(
				private val stream: InputStream,
				private val sftp: SftpClient
		) : InputStream() {
			final override fun read(): Int = stream.read()
			override fun close() {
				silent { stream.close() }
				silent { sftp.close() }
			}
		}

		/**
		 * InputStream proxy that closes the session when closing the input stream
		 * and therefore fit for data connections
		 */
		class DataSessionInputStream(
				stream: InputStream,
				sftp: SftpClient,
				private val session: ClientSession) : ControlSessionInputStream(stream, sftp) {
			override fun close() {
				super.close()
				session.close()
			}
		}

	}

	class SessionCloseListener(
			private val host: Host,
			private val hostDynamicDao: HostDynamicDao,
			private val connections: MutableMap<UUID, Pair<ClientSession, Distribution>>
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

	var sshServerPort: Int = defaultSshServerPort
	var sshUserName: String = defaultSshUserName
	private val connections = Collections.synchronizedMap(hashMapOf<UUID, Pair<ClientSession, Distribution>>())

	override fun connectHost(host: Host) {
		checkAddressNotLocal(host.address)
		logger.info("Connecting to host {} {}", host.id, host.address)
		val session = sshClientService.loginWithPublicKey(
				address = host.address,
				hostPublicKey = host.publicKey)
		session.addSessionListener(SessionCloseListener(host, hostDynamicDao, connections))
		val distro = discoverer.detectDistro(session)
		if (distro != null) {
			connections.put(host.id, session to distro)
			logger.debug("starting host monitoring processes on {} {}", host.address, host.id)
			if (host.dedicated) {
				distro.installMonitorPackages(session)
				hostDao.update(host)
			}
			distro.startMonitorProcesses(session, host, hostDynamicDao)
		}
		val hypervisor = getHypervisor(host)
		if (hypervisor != null) {
			logger.debug("starting vm monitoring processes on {} {}", host.address, host.id)
			hypervisor.startMonitoringProcess()
		} else {
			logger.info("Host {} {} does not have a hypervisor, no vm monitoring started", host.address, host.id)
		}
	}

	internal fun checkAddressNotLocal(address: String) {
		try {
			val addr = resolve(address)
			if (addr.isLoopbackAddress || addr.isLinkLocalAddress || addr.isAnyLocalAddress) {
				throw HostAddressException("$address is local")
			}
		} catch (hnf: UnknownHostException) {
			logger.info("$address host address resolution failed", hnf)
			throw HostAddressException("$address can't be resolved")
		}
	}

	open internal fun resolve(address: String) = InetAddress.getByName(address)

	override fun join(host: Host, password: String, powerManagers: List<PowerManagementInfo>): Host {
		val session = sshClientService.loginWithPassword(
				address = host.address,
				userName = "root",
				password = password,
				hostPublicKey = host.publicKey)
		sshClientService.installPublicKey(session)

		return joinConnectedHost(host, session, powerManagers)
	}

	override fun join(host: Host, powerManagers: List<PowerManagementInfo>): Host {
		val session = sshClientService.loginWithPublicKey(
				address = host.address,
				userName = "root",
				hostPublicKey = host.publicKey
		)
		return joinConnectedHost(host, session, powerManagers)
	}

	internal fun joinConnectedHost(host: Host, session: ClientSession, powerManagers: List<PowerManagementInfo>): Host {
		val capabilities = discoverer.discoverHost(session)

		val internalHost = host.copy(capabilities = capabilities.copy(
				powerManagment = capabilities.powerManagment + powerManagers
		))

		hostDao.add(internalHost)
		hostAssigner.assignController(host)

		return host
	}

	private fun connectHosts() {
		hostAssignmentDao.listByControllerAndType(controllerManager.getControllerId(), AssignmentType.host).filterNot {
			connections.containsKey(it.entityId)
		}.forEach {
			logger.info("connecting assigned host {}", it.entityId)
			val host = hostDao[it.entityId]
			if (host != null) {
				//TODO: this try-catch should be temporary, refactor to a threadpool
				//anyway it would be a bad idea to wait for 100+ hosts to be connected
				//over a slow, unreliable network
				try {
					connectHost(host)
				} catch (e: Exception) {
					logger.error("Could not connect host {} at {}", host.id, host.address, e)
					hostDynamicDao.remove(host.id)
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

		var serverKey: PublicKey? = null
	}

	override fun getHostPublicKey(address: String): PublicKey {
		checkAddressNotLocal(address)
		val pubKeySshClient = SshClient.setUpDefaultClient()!!
		val serverKeyReader = ServerKeyReader()
		pubKeySshClient.serverKeyVerifier = serverKeyReader
		pubKeySshClient.start()
		try {
			val connect = pubKeySshClient.connect(sshUserName, address, sshServerPort)!!

			connect.await()
			val session = connect.session!!
			session.auth()!!.await()
			logger.info(session.serverVersion)
			return serverKeyReader.serverKey!!
		} finally {
			pubKeySshClient.stop()
		}

	}

}