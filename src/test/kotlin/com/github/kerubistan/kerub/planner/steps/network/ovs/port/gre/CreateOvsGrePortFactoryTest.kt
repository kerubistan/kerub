package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class CreateOvsGrePortFactoryTest : AbstractFactoryVerifications(CreateOvsGrePortFactory) {

	@Test
	fun produce() {

		assertTrue("there are two hosts, both have the test network - but they are already connected") {
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
			val steps = factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost, testOtherHost),
							hostDyns = listOf(hostUp(testHost), hostUp(testOtherHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsGrePort(
																			name = "gre0",
																			remoteAddress = testOtherHost.address
																	)
															)
													)
											)
									),
									HostConfiguration(
											id = testOtherHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsGrePort(
																			name = "gre0",
																			remoteAddress = testHost.address
																	)
															)
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(vm),
							vmDyns = listOf(
									vmUp(vm, testHost)
							)
					))

			steps.isEmpty()

		}

		assertTrue("only one host have the network created - nothing to offer") {
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
			val steps = factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost, testOtherHost),
							hostDyns = listOf(hostUp(testHost), hostUp(testOtherHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									),
									HostConfiguration(
											id = testOtherHost.id,
											networkConfiguration = listOf(
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(vm),
							vmDyns = listOf(
									vmUp(vm, testHost)
							)
					))

			steps.isEmpty()

		}

		assertTrue("there are two hosts, both have the test network - let's offer creating a GRE port") {
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
			val steps = factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost, testOtherHost),
							hostDyns = listOf(hostUp(testHost), hostUp(testOtherHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									),
									HostConfiguration(
											id = testOtherHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id
													)
											)
									)
							),
							virtualNetworks = listOf(testVirtualNetwork),
							vms = listOf(vm),
							vmDyns = listOf(
									vmUp(vm, testHost)
							)
					))

			steps.any { step ->
				step is CreateOvsGrePort
						&& step.firstHost == testHost
						&& step.secondHost == testOtherHost
						&& step.virtualNetwork == testVirtualNetwork
			}

		}
	}
}