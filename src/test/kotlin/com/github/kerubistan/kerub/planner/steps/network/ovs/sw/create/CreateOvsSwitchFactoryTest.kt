package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class CreateOvsSwitchFactoryTest : AbstractFactoryVerifications(CreateOvsSwitchFactory) {

	@Test
	fun produce() {
		assertTrue("All ok - produce a step") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							installedSoftware = listOf(
									pack("openvswitch-switch", "2.9.2")
							)
					)
			)
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(
									adapterType = NetworkAdapterType.virtio,
									networkId = testVirtualNetwork.id
							)
					),
					expectations = listOf(
							VirtualMachineAvailabilityExpectation()
					)
			)
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vms = listOf(vm),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			) == listOf(
					CreateOvsSwitch(
							host = host,
							network = testVirtualNetwork
					)
			)
		}
	}
}