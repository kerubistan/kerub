package com.github.kerubistan.kerub.host.fw

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.junit.Before
import org.junit.Test

class IptablesFireWallTest {

	val session: ClientSession = mock()
	val channel: ChannelExec = mock()

	@Before
	fun setup() {
		whenever(session.createExecChannel(any())).thenReturn(channel)
		whenever(channel.invertedErr).thenReturn(NullInputStream(0))
		whenever(channel.invertedOut).thenReturn(NullInputStream(0))
		whenever(channel.open()).thenReturn(mock())
	}

	@Test
	fun testOpen() {
		IptablesFireWall(session).open(1234, "tcp")
		verify(channel).open()
		verify(session).createExecChannel(any())
	}

	@Test
	fun testClose() {
		IptablesFireWall(session).close(1234, "tcp")
		verify(channel).open()
		verify(session).createExecChannel(any())
	}
}