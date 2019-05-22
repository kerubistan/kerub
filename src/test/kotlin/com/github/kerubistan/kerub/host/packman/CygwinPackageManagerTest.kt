package com.github.kerubistan.kerub.host.packman

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resource
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.startsWith

class CygwinPackageManagerTest {

	val session: ClientSession = mock()
	private val wmicExecChannel: ChannelExec = mock()
	private val cygchheckChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()


	@Test
	fun list() {
		whenever(session.createExecChannel(startsWith("wmic"))).thenReturn(wmicExecChannel)
		whenever(wmicExecChannel.open()).thenReturn(openFuture)
		whenever(wmicExecChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(wmicExecChannel.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/cygwin/wmic-product-list.txt")
		}

		whenever(session.createExecChannel(startsWith("cygcheck"))).thenReturn(cygchheckChannel)
		whenever(cygchheckChannel.open()).thenReturn(openFuture)
		whenever(cygchheckChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(cygchheckChannel.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/cygwin/cygcheck-d-c.txt")
		}

		val packages = CygwinPackageManager(session).list()

		//from the cygwin package list
		assertTrue(packages.any { it.name == "sed" })
		//from wmic
		assertTrue(packages.any { it.name == "virtualbox" })
	}
}