package com.github.kerubistan.kerub.planner.steps.network.ovs.port.create

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import org.junit.jupiter.api.Test

import kotlin.test.assertTrue

internal class CreateOvsPortFactoryTest : AbstractFactoryVerifications(CreateOvsPortFactory) {

	@Test
	fun produce() {

		assertTrue("vm must start, host config blank (no switch), we can't offer a port") {
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(
									networkId = testVirtualNetwork.id,
									adapterType = NetworkAdapterType.virtio
							)
					),
					expectations = listOf(VirtualMachineAvailabilityExpectation())
			)
			CreateOvsPortFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							// no host config
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(vm)
					)
			).isEmpty()
		}

		assertTrue("vm must start, no switch yet, we can't offer a port") {
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(
									networkId = testVirtualNetwork.id,
									adapterType = NetworkAdapterType.virtio
							)
					),
					expectations = listOf(VirtualMachineAvailabilityExpectation())
			)
			CreateOvsPortFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											// no switch
											networkConfiguration = listOf(
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(
									vm
							)
					)
			).isEmpty()
		}

		assertTrue("vm must start, virtual switch created, also port - let's not repeat it") {
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(
									networkId = testVirtualNetwork.id,
									adapterType = NetworkAdapterType.virtio
							)
					),
					expectations = listOf(VirtualMachineAvailabilityExpectation())
			)
			CreateOvsPortFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															// port already created for vm
															ports = listOf(OvsDataPort(vm.idStr))
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(
									vm
							)
					)
			).isEmpty()
		}

		assertTrue("vm must start, virtual switch created  - lets offer to create port") {
			val vm = testVm.copy(
					devices = listOf(
							NetworkDevice(
									networkId = testVirtualNetwork.id,
									adapterType = NetworkAdapterType.virtio
							)
					),
					expectations = listOf(VirtualMachineAvailabilityExpectation())
			)
			CreateOvsPortFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf() // no port yet
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(
									vm
							)
					)
			) == listOf(
					CreateOvsPort(host = testHost, virtualNetwork = testVirtualNetwork, portName = testVm.idStr)
			)
		}
	}
}