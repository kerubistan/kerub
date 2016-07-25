package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Matchers

class CygwinPackageManagerTest {

	val session: ClientSession = mock()
	val wmicExecChannel: ChannelExec = mock()
	val cygchheckChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()


	@Test
	fun list() {
		whenever(session.createExecChannel(Matchers.startsWith("wmic"))).thenReturn(wmicExecChannel)
		whenever(wmicExecChannel.open()).thenReturn(openFuture)
		whenever(wmicExecChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(wmicExecChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/cygwin/wmic-product-list.txt")
		}

		whenever(session.createExecChannel(Matchers.startsWith("cygcheck"))).thenReturn(cygchheckChannel)
		whenever(cygchheckChannel.open()).thenReturn(openFuture)
		whenever(cygchheckChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(cygchheckChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/cygwin/cygcheck-d-c.txt")
		}

		val packages = CygwinPackageManager(session).list()

		//from the cygwin package list
		assertTrue(packages.any { it.name == "sed" })
		//from wmic
		assertTrue(packages.any { it.name == "virtualbox" })
	}
}