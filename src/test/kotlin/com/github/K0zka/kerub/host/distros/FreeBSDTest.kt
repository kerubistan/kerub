package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

import org.junit.Assert.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

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

		assertEquals("1040261120 B".toSize(), totalMem)
	}



}