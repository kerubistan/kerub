package com.github.kerubistan.kerub.utils.junix.lsblk

import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertEquals

class LsblkTest {

	private val session: ClientSession = mock()
	private val future: OpenFuture = mock()
	private val exec: ChannelExec = mock()

	@Test
	fun list() {
		whenever(session.createExecChannel(argThat { startsWith("lsblk") })).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/lsblk/lsblk-centos7.txt") }
		whenever(exec.invertedErr).then { NullInputStream(0) }

		val list = Lsblk.list(session)

		verify(session).createExecChannel(argThat { startsWith("lsblk") })
		assertEquals(3, list.size)
	}
}