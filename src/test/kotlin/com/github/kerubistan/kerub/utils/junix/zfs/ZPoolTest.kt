package com.github.kerubistan.kerub.utils.junix.zfs

import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resource
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ZPoolTest {

	private val session: ClientSession = mock()
	private val openFuture: OpenFuture = mock()
	private val channelExec: ChannelExec = mock()

	@Test
	fun listWithFreeBsd11() {
		whenever(session.createExecChannel(any())).thenReturn(channelExec)
		whenever(channelExec.open()).thenReturn(openFuture)
		whenever(channelExec.invertedOut).thenReturn(resource("com/github/kerubistan/kerub/utils/junix/zfs/zpool-list-freebsd11.txt"))
		whenever(channelExec.invertedErr).thenReturn(NullInputStream(0))

		val zpools = ZPool.list(session)
		assertEquals(2, zpools.size)
		assertEquals("test", zpools[0].name)
		assertFalse(zpools[0].readOnly)
		assertEquals("14561816953702211935", zpools[0].id)
		assertEquals("1.35M".toSize(), zpools[0].allocated)
	}
}