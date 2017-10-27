package com.github.kerubistan.kerub.utils.junix.sysctl

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Before
import org.junit.Test

class BsdSysCtlTest {

	val session : ClientSession = mock()
	val future : OpenFuture = mock()
	val execChannel : ChannelExec = mock()

	@Before
	fun setUp() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
	}

	@Test
	fun getCpuFlags() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		//whenever(execChannel.invertedOut).thenReturn()
	}
}