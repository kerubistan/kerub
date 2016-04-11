package com.github.K0zka.kerub.utils.junix

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.on
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
open class AbstractJunixCommandVerification {

	@Mock
	var session : ClientSession? = null
	@Mock
	var execChannel : ChannelExec? = null
	@Mock
	var openFuture : OpenFuture? = null

	@Before
	fun setupMocks() {
		on(session!!.createExecChannel(anyString())).thenReturn(execChannel!!)
		on(execChannel!!.open()).thenReturn(openFuture!!)
	}

}