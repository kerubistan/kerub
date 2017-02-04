package com.github.K0zka.kerub.utils.junix.ovs

import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Test
import kotlin.test.assertEquals

class VsctlTest : AbstractJunixCommandVerification() {
	@Test
	fun listBridges() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).then { resource("com/github/K0zka/kerub/utils/junix/ovs/singlebridge.csv") }
		val bridges = Vsctl.listBridges(session)
		assertEquals(1, bridges.size)
	}

	@Test
	fun listPorts() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).then { resource("com/github/K0zka/kerub/utils/junix/ovs/singleport.csv") }
		val ports = Vsctl.listPorts(session)
		assertEquals(1, ports.size)
	}
}