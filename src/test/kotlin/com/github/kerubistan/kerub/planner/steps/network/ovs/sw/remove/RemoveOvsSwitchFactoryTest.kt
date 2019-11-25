package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove

import com.github.kerubistan.kerub.hostDown
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
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

internal class RemoveOvsSwitchFactoryTest : AbstractFactoryVerifications(RemoveOvsSwitchFactory) {

	@Test
	fun produce() {
		assertTrue("no vms, no activity, but can't remove because the host is down as well") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostDown(testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			).isEmpty()
		}

		assertTrue("there is a vm, but it is using the vnet, let's not remove the switch") {
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(adapterType = NetworkAdapterType.ne2k_pci, networkId = testVirtualNetwork.id)
					)
			)
			factory.produce(
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
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			).isEmpty()
		}

		assertTrue("there is a vm, but no network used, let's remove the switch") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							vms = listOf(testVm),
							vmDyns = listOf(vmUp(testVm, testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			) == listOf(RemoveOvsSwitch(host = testHost, virtualNetwork = testVirtualNetwork))
		}

		assertTrue("no vms, no activity, let's remove the switch because it is not needed") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork)
					)
			) == listOf(RemoveOvsSwitch(host = testHost, virtualNetwork = testVirtualNetwork))
		}
	}
}