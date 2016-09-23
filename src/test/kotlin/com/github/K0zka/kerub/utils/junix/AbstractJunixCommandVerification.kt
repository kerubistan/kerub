package com.github.K0zka.kerub.utils.junix

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.on
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

open class AbstractJunixCommandVerification {

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val openFuture : OpenFuture = mock()

	@Before
	fun setupMocks() {
		whenever(session.createExecChannel(anyString())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
	}

}