package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.getTestKey
import com.github.K0zka.kerub.model.Host
import org.apache.sshd.SshServer
import org.apache.sshd.server.Command
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.auth.UserAuthPublicKey
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.test.assertEquals

RunWith(MockitoJUnitRunner::class)
public class HostManagerImplTest {

	Mock
	var hostDao: HostDao? = null

	Mock
	var hostDynamicDao : HostDynamicDao? = null

	Mock
	var sshClientService : SshClientService? = null

	var hostManager : HostManagerImpl? = null

	var sshServer: SshServer? = null
	var shell : TestShellCommand? = null

	class TestShellCommand : Command {

		var input : InputStream? = null
		var output : OutputStream? = null
		var error : OutputStream? = null
		var env : Environment? = null
		var destroyed : Boolean = false

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

	Before
	fun setup() {
		val key = getTestKey()
		hostManager = HostManagerImpl(getTestKey(), hostDao!!, hostDynamicDao!!, sshClientService!!)
		hostManager!!.sshServerPort = 2022
		shell = TestShellCommand()
		sshServer = SshServer.setUpDefaultServer()
		sshServer!!.setPort(2022)
		sshServer!!.setUserAuthFactories(listOf(UserAuthPublicKey.Factory()))
		sshServer!!.setKeyPairProvider( SingleKeyPairProvider(key) )
		sshServer!!.setShellFactory { shell }
		sshServer!!.start()
	}

	After
	fun cleanup() {
		sshServer!!.stop()
	}

	Test
	fun getHostPubkey() {
		val publicKey = hostManager!!.getHostPublicKey("localhost")
		assertEquals( getTestKey().getPublic(), publicKey )
	}

	Test
	fun connectHost() {
		val host = Host(id = UUID.randomUUID(),
		                address = "127.0.0.1",
		                capabilities = null,
		                dedicated = false,
		                publicKey = "")
		hostManager!!.connectHost(host)
		Thread.sleep(1000)
	}
}