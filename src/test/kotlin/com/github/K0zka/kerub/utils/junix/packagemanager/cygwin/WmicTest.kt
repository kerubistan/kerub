package com.github.K0zka.kerub.utils.junix.packagemanager.cygwin

import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

import org.junit.Assert.*

class WmicTest {

	val session: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()

	@Test
	fun list() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/cygwin/wmic-product-list.txt")
		}

		val installed = Wmic.list(session)

		assertFalse(installed.isEmpty())
	}
}