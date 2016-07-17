package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doCallRealMethod
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

import org.junit.Assert.*
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class AbstractLinuxTest {

	val session : ClientSession = mock()
	val future : OpenFuture = mock()
	val exec : ChannelExec = mock()

	@Test
	fun getTotalMemory() {
		val linux = Fedora()  // for example (do this better with mockito)
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenReturn(
				ByteArrayInputStream("MemTotal:       16345292 kB".toByteArray(Charset.forName("US-ASCII")))
		)

		val total = linux.getTotalMemory(session)
		assertEquals("16345292 kB".toSize(), total)
	}
}