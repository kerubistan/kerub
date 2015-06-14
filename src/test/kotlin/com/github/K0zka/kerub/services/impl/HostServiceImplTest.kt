package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.host.ControllerAssigner
import com.github.K0zka.kerub.host.HostCapabilitiesDiscoverer
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.SshClientService
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.services.HostAndPassword
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

RunWith(MockitoJUnitRunner::class)
public class HostServiceImplTest {
	Mock
	var dao: HostDao? = null
	Mock
	var manager: HostManager? = null
	Mock
	var hostAssigner: ControllerAssigner? = null
	Mock
	var sshClientService: SshClientService? = null
	Mock
	var discoverer: HostCapabilitiesDiscoverer? = null

	var service : HostServiceImpl? = null

	Before
	fun setup() {
		service = HostServiceImpl(dao!!, manager!!, hostAssigner!!, sshClientService!!, discoverer!!)
	}

	Test
	fun join() {
		val hostAndPwd = HostAndPassword(
				password = "TEST",
		        host = Host(
				        id = UUID.randomUUID(),
		                address = "127.0.0.1",
		                publicKey = "TEST",
		                dedicated = true,
		                capabilities = null
		                   )
		                                )
		service!!.join(hostAndPwd)
	}
}