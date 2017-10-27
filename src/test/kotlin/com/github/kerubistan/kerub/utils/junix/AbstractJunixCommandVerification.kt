package com.github.kerubistan.kerub.utils.junix

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Before

open class AbstractJunixCommandVerification {

	val session: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()

	@Before
	fun setupMocks() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
	}

}