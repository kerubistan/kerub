package com.github.kerubistan.kerub.utils.junix.ovs

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import io.github.kerubistan.kroki.io.resourceToString
import org.junit.Test
import kotlin.test.assertEquals

class VsctlTest : AbstractJunixCommandVerification() {

	@Test
	fun createBridge() {
		session.mockCommandExecution("ovs-vsctl.*".toRegex())
		Vsctl.createBridge(session, "br0")
		session.verifyCommandExecution("ovs-vsctl.*br0.*".toRegex())
	}

	@Test
	fun listBridges() {
		session.mockCommandExecution(
				commandMatcher = "^ovs-vsctl list br$".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/utils/junix/ovs/singlebridge.csv")
		)
		val bridges = Vsctl.listBridges(session)
		assertEquals(1, bridges.size)
	}

	@Test
	fun listPorts() {
		session.mockCommandExecution(
				commandMatcher = "^ovs-vsctl list port$".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/utils/junix/ovs/singleport.csv")
		)
		val ports = Vsctl.listPorts(session)
		assertEquals(1, ports.size)
	}
}