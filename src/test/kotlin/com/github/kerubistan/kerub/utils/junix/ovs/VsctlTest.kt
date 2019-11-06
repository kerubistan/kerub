package com.github.kerubistan.kerub.utils.junix.ovs

import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import io.github.kerubistan.kroki.io.resourceToString
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VsctlTest : AbstractJunixCommandVerification() {

	@Test
	fun available() {
		assertFalse(Vsctl.available(null))
		assertFalse(Vsctl.available(testHostCapabilities))
		assertTrue(Vsctl.available(testHostCapabilities.copy(
				installedSoftware = listOf(
						pack("openvswitch-switch", "2.9.2")
				)
		)))
	}

	@Test
	fun createBridge() {
		session.mockCommandExecution("ovs-vsctl.*".toRegex())
		Vsctl.createBridge(session, "br0")
		session.verifyCommandExecution("ovs-vsctl.*br0.*".toRegex())
	}

	@Test
	fun createPort() {
		session.mockCommandExecution("^ovs-vsctl add-port br0 port0$".toRegex())
		Vsctl.createPort(session, "br0", "port0")
		session.verifyCommandExecution("^ovs-vsctl add-port br0 port0$".toRegex())
	}

	@Test
	fun createInternalPort() {
		session.mockCommandExecution(
				"^ovs-vsctl add-port br0 port0 -- set Interface port0 type=internal".toRegex()
		)
		Vsctl.createInternalPort(session, "br0", "port0")
		session.verifyCommandExecution(
				"^ovs-vsctl add-port br0 port0 -- set Interface port0 type=internal$".toRegex()
		)
	}

	@Test
	fun createGrePort() {
		session.mockCommandExecution(
				"^ovs-vsctl add-port br0 gre0 -- set Interface gre0 type=gre .*".toRegex()
		)
		Vsctl.createGrePort(session, "br0", "gre0", "example.com")
		session.verifyCommandExecution(
				"^ovs-vsctl add-port br0 gre0 -- set Interface gre0 type=gre .*".toRegex()
		)

	}

	@Test
	fun removeBridge() {
		session.mockCommandExecution("^ovs-vsctl del-br br0$".toRegex())
		Vsctl.removeBridge(session, "br0")
		session.verifyCommandExecution("^ovs-vsctl del-br br0$".toRegex())
	}

	@Test
	fun removePort() {
		session.mockCommandExecution("^ovs-vsctl del-port br0 port0$".toRegex())
		Vsctl.removePort(session, "br0", "port0")
		session.verifyCommandExecution("^ovs-vsctl del-port br0 port0$".toRegex())
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