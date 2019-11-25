package com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class RemoveOvsPortFactoryTest : AbstractFactoryVerifications(RemoveOvsPortFactory) {

	@Test
	fun produce() {
		assertTrue("port is used by a vm, do not remove") {
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(adapterType = NetworkAdapterType.virtio, networkId = testVirtualNetwork.id)
					)
			)
			RemoveOvsPortFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							vms = listOf(vm),
							vmDyns = listOf(vmUp(vm, testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(vm.idStr)
															)
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			).isEmpty()
		}
		assertTrue("Not used virtual network on a host - offer to remove") {
			RemoveOvsPortFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort("test-port")
															)
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			) == listOf(
					RemoveOvsPort(
							host = testHost,
							portName = "test-port",
							virtualNetwork = testVirtualNetwork
					)
			)
		}
	}

}