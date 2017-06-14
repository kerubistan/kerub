package com.github.K0zka.kerub.utils.junix.dmesg

import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertTrue
import org.junit.Test

class BsdDmesgTest {

	val session : ClientSession = mock()
	val openFuture : OpenFuture = mock()
	val channelExec : ChannelExec = mock()

	@Test
	fun listCpuFlags() {
		whenever(session.createExecChannel(any())).thenReturn(channelExec)
		whenever(channelExec.open()).thenReturn(openFuture)

		whenever(channelExec.invertedErr).thenReturn(NullInputStream(0))
		whenever(channelExec.invertedOut).thenReturn(resource("com/github/K0zka/kerub/utils/junix/dmesg/bsd-dmesg-flags.txt"))

		val flags = BsdDmesg.listCpuFlags(session)

		assertTrue(flags.contains("sse"))
		assertTrue(flags.contains("sse2"))
		assertTrue(flags.contains("vmx"))
	}

	@Test
	fun listCpuFlagsWithAmd() {
		whenever(session.createExecChannel(any())).thenReturn(channelExec)
		whenever(channelExec.open()).thenReturn(openFuture)

		whenever(channelExec.invertedErr).thenReturn(NullInputStream(0))
		whenever(channelExec.invertedOut).thenReturn(resource("com/github/K0zka/kerub/utils/junix/dmesg/bsd-dmesg-freebsd10-amd.txt"))

		val flags = BsdDmesg.listCpuFlags(session)

		assertTrue(flags.contains("svm"))
	}

}