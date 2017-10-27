package com.github.kerubistan.kerub.utils.junix.fw.iptables

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.junit.Before
import org.junit.Test

class IpTablesTest {

	val session : ClientSession = mock()
	val channel : ChannelExec = mock()

	@Before
	fun setup() {
		whenever(session.createExecChannel(any())).thenReturn(channel)
		whenever(channel.invertedErr).thenReturn(NullInputStream(0))
		whenever(channel.invertedOut).thenReturn(NullInputStream(0))
		whenever(channel.open()).thenReturn(mock())
	}

	@Test
	fun testOpen() {
		IpTables.open(session, 1234, "tcp")
	}

	@Test
	fun testClose() {
		IpTables.close(session, 1234, "tcp")
	}
}