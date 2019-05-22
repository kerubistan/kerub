package com.github.kerubistan.kerub.utils.junix.ifconfig

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resource
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Test

class IfConfigTest {

	val session: ClientSession = mock()
	private val exec: ChannelExec = mock()
	val future: OpenFuture = mock()

	@Test
	fun listWithLinux() {
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenAnswer {
			resource("com/github/kerubistan/kerub/utils/junix/ifconfig/linuxsample.txt")
		}

		val list = IfConfig.list(session)

		assertEquals(3, list.size)
		assertEquals("enp2s0", list[0].name)
		assertEquals(1500, list[0].mtu)
		assertEquals("68:f7:28:e2:29:e5", list[0].mac)
		assertEquals("lo", list[1].name)
		assertEquals(65536, list[1].mtu)
		assertEquals("virbr0", list[2].name)
		assertEquals(1500, list[2].mtu)
	}

	@Test
	fun listWithBsd() {
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenAnswer {
			resource("com/github/kerubistan/kerub/utils/junix/ifconfig/bsdsample.txt")
		}

		val list = IfConfig.list(session)

		assertEquals(2, list.size)
		assertEquals("vtnet0", list[0].name)
		assertEquals(1500, list[0].mtu)
	}

}