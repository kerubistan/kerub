package com.github.kerubistan.kerub.utils.junix.bridgectl

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BridgeCtlTest {

	val session : ClientSession = mock()

	private val testOutput = """bridge name	bridge id		STP enabled	interfaces
virbr0		8000.fe5400194644	yes		vnet0
							vnet1
							vnet2
virbr1		8000.000000000000	yes
"""

	@Test
	fun list() {
		session.mockCommandExecution("brctl show".toRegex(), output = testOutput)

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