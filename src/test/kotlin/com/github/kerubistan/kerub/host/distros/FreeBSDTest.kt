package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.model.Version
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class FreeBSDTest {

	val session : ClientSession = mock()
	val future : OpenFuture = mock()
	val exec : ChannelExec = mock()

	@Test
	fun handlesVersion() {
		assertTrue(FreeBSD().handlesVersion(Version.fromVersionString("10.0")))
		assertTrue(FreeBSD().handlesVersion(Version.fromVersionString("11.0.RC3")))
	}

	@Test
	fun detect() {
		whenever(session.createExecChannel(eq("uname -s"))).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenReturn(
				ByteArrayInputStream("FreeBSD".toByteArray())
		)

		assertTrue(FreeBSD().detect(session))
	}

	@Test
	fun detectHostCpuType() {
		whenever(session.createExecChannel(eq("uname -p"))).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenReturn(
				ByteArrayInputStream("amd64".toByteArray())
		)

		assertEquals("X86_64", FreeBSD().detectHostCpuType(session))
	}

	@Test
	fun getTotalMemory() {
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenReturn(
				ByteArrayInputStream("hw.physmem: 1040261120".toByteArray())
		)

		val totalMem = FreeBSD().getTotalMemory(session)

		assertEquals(1040261120.toBigInteger(), totalMem)
	}



}