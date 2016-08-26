package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.exc.HostAddressException
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.getTestKey
import com.github.K0zka.kerub.host.distros.Distribution
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.controller.AssignmentType
import com.github.K0zka.kerub.never
import com.github.K0zka.kerub.on
import com.github.K0zka.kerub.verify
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.server.Command
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import sun.security.krb5.internal.HostAddress
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class) class HostManagerImplTest {

	@Mock
	var hostDao: HostDao? = null

	@Mock
	var hostDynamicDao: HostDynamicDao? = null

	@Mock
	var vmDynDao: VirtualMachineDynamicDao? = null

	@Mock
	var sshClientService: SshClientService? = null

	@Mock
	var controllerManager: ControllerManager? = null

	@Mock
	var hostAssignmentDao: AssignmentDao? = null

	@Mock
	var virtualStorageDao: VirtualStorageDeviceDao? = null
	@Mock
	var virtualStorageDynDao: VirtualStorageDeviceDynamicDao? = null

	@Mock
	var hostAssigner: ControllerAssigner? = null

	@Mock
	var discoverer: HostCapabilitiesDiscoverer? = null

	@Mock
	val clientSession: ClientSession? = null

	val hypervisor: Hypervisor = mock()

	var hostManager: HostManagerImpl? = null

	var sshServer: SshServer? = null
	var shell: TestShellCommand? = null

	@Mock
	var distro: Distribution? = null

	class TestShellCommand : Command {

		var input: InputStream? = null
		var output: OutputStream? = null
		var error: OutputStream? = null
		var env: Environment? = null
		var destroyed: Boolean = false

		override fun setInputStream(`in`: InputStream?) {
			input = `in`
		}

		override fun setOutputStream(out: OutputStream?) {
			output = out
		}

		override fun setErrorStream(err: OutputStream?) {
			error = err
		}

		override fun setExitCallback(callback: ExitCallback?) {
		}

		override fun start(env: Environment?) {
			this.env = env
		}

		override fun destroy() {
			this.destroyed = true
		}

	}

	@Before
	fun setup() {
		val key = getTestKey()
		hostManager = spy(
				HostManagerImpl(
						hostDao!!,
						hostDynamicDao!!,
						vmDynDao!!,
						virtualStorageDao!!,
						virtualStorageDynDao!!,
						sshClientService!!,
						controllerManager!!,
						hostAssignmentDao!!,
						discoverer!!,
						hostAssigner!!
				)
		)
		hostManager!!.sshServerPort = 2022
		shell = TestShellCommand()
		sshServer = SshServer.setUpDefaultServer()
		sshServer!!.port = 2022
		sshServer!!.userAuthFactories = listOf(UserAuthPublicKeyFactory())
		sshServer!!.keyPairProvider = SingleKeyPairProvider(key)
		sshServer!!.setShellFactory { shell }
		sshServer!!.start()
	}

	@After
	fun cleanup() {
		sshServer!!.stop()
	}

	@Test
	fun getHostPubkey() {
		whenever(hostManager!!.resolve(any())).thenReturn(InetAddress.getLocalHost())
		val publicKey = hostManager!!.getHostPublicKey("localhost")
		assertEquals(getTestKey().public, publicKey)
	}

	@Test
	fun connectHost() {
		val host = Host(id = UUID.randomUUID(),
				address = "host1.example.com",
				capabilities = null,
				dedicated = false,
				publicKey = "")
		whenever(hostManager!!.resolve(any())).thenReturn(InetAddress.getLocalHost())
		on(sshClientService!!.loginWithPublicKey(
				address = anyString(),
				hostPublicKey = anyString(),
				userName = anyString())).thenReturn(clientSession)
		hostManager!!.connectHost(host)
		Thread.sleep(1000)
	}

	@Test
	fun connectHostWithNotExisting() {
		val host = Host(id = UUID.randomUUID(),
				address = "host1.example.com",
				capabilities = null,
				dedicated = false,
				publicKey = "")
		whenever(hostManager!!.resolve(any())).then { throw UnknownHostException("TEST") }
		on(sshClientService!!.loginWithPublicKey(
				address = anyString(),
				hostPublicKey = anyString(),
				userName = anyString())).thenReturn(clientSession)
		expect(HostAddressException::class) {
			hostManager!!.connectHost(host)
		}
		verify(sshClientService!!, never).loginWithPublicKey(any(), any(), any())
	}

	@Test
	fun start() {
		val controllerId = "test controller id"
		val hostId = UUID.randomUUID()
		val host = Host(id = hostId, address = "host.example.com", dedicated = true, publicKey = "testkey")
		on(controllerManager!!.getControllerId()).thenReturn(controllerId)
		on(hostAssignmentDao!!.listByControllerAndType(eq(controllerId), eq(AssignmentType.host))).thenReturn(
				listOf(Assignment(controller = controllerId, entityId = hostId, type = AssignmentType.host))
		)
		on(hostDao!!.get(hostId)).thenReturn(host)
		on(sshClientService!!.createSession(anyString(), anyString())).thenReturn(clientSession)
		on(hostManager!!.resolve(any())).thenReturn(InetAddress.getLocalHost())
		on(sshClientService!!.loginWithPublicKey(any(), any(), any())).thenReturn(clientSession)

		hostManager!!.start()

		verify(sshClientService)!!.loginWithPublicKey(eq(host.address), anyString(), eq(host.publicKey))

	}

	@Test
	fun dataConnection() {
		on(sshClientService!!.loginWithPublicKey(anyString(), anyString(), anyString())).thenReturn(clientSession)

		val hostId = UUID.randomUUID()
		val host = Host(id = hostId, address = "host.example.com", dedicated = true, publicKey = "testkey")
		var called = false
		hostManager!!.dataConnection(host, {
			session ->
			called = true
			assertEquals(clientSession, session)
		})

		assertTrue(called)
		verify(sshClientService!!).loginWithPublicKey(anyString(), anyString(), anyString())
	}

	@Test
	@Ignore("TODO: not finished, it needs the hypervisor separation")
	fun execute() {
		val hostId = UUID.randomUUID()
		val host = Host(id = hostId, address = "host.example.com", dedicated = true, publicKey = "testkey")
		whenever(discoverer!!.detectDistro(eq(clientSession!!))).thenReturn(distro)
		whenever(sshClientService!!.loginWithPublicKey(eq(host.address), anyString(), eq(host.publicKey))).thenReturn(clientSession)

		hostManager!!.connectHost(host)
		val result = hostManager!!.execute(host) {
			"PASS"
		}
		assertEquals("PASS", result)
	}

	@Test
	fun getFireWall() {
		hostManager
	}

	@Test
	fun checkAddressNotLocal() {
		expect(HostAddressException::class) {
			hostManager!!.checkAddressNotLocal("127.0.0.1")
		}
		expect(HostAddressException::class) {
			hostManager!!.checkAddressNotLocal("127.0.0.5")
		}
		expect(HostAddressException::class) {
			hostManager!!.checkAddressNotLocal("127.1.2.3")
		}
		expect(HostAddressException::class) {
			hostManager!!.checkAddressNotLocal("localhost")
		}
	}

	@Ignore
	@Test
	fun stop() {
		TODO()
	}
}