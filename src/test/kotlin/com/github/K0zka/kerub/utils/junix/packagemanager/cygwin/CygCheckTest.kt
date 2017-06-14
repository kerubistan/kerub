package com.github.K0zka.kerub.utils.junix.packagemanager.cygwin

import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CygCheckTest {

	val session: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()

	@Test
	fun testListPackages() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/cygwin/cygcheck-d-c.txt")
		}

		val packages = CygCheck.listPackages(session)

		assertFalse(packages.isEmpty())
		assertTrue(packages.any { it.name == "sed" && it.version.major == "4" && it.version.minor == "2" })
		verify(execChannel).close(true)
	}
}