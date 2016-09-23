package com.github.K0zka.kerub.utils.junix.bridgectl

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream

class BridgeCtlTest {

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val channelOpenFuture : OpenFuture = mock()

	val testOutput = """bridge name	bridge id		STP enabled	interfaces
virbr0		8000.fe5400194644	yes		vnet0
							vnet1
							vnet2
virbr1		8000.000000000000	yes
"""

	@Test
	fun list() {
		Mockito.`when`(session.createExecChannel(any<String>())).thenReturn(execChannel)
		Mockito.`when`(execChannel.open()).thenReturn(channelOpenFuture)
		Mockito.`when`(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val bridges = BridgeCtl.list(session)

		assertEquals(2, bridges.size)

		assertEquals(listOf("vnet0","vnet1","vnet2"), bridges[0].ifaces)
		assertEquals("virbr0", bridges[0].name)
		assertEquals("8000.fe5400194644", bridges[0].id)

		assertTrue(bridges[1].ifaces.isEmpty())
		assertEquals("virbr1", bridges[1].name)
		assertEquals("8000.000000000000", bridges[1].id)
	}

}