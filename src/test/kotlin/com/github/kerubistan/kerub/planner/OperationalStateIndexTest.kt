package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.hostDown
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class OperationalStateIndexTest {

	@Test
	fun getHostVirtualNetworks() {
		assertTrue("blank state - no virtual networks") {
			OperationalState.fromLists().index.hostVirtualNetworks.isEmpty()
		}
		assertTrue("") {
			OperationalState.fromLists(
					hosts = listOf(testHost),
					hostDyns = listOf(hostUp(testHost)),
					hostCfgs = listOf(
							HostConfiguration(
									id = testHost.id,
									networkConfiguration = listOf(
											OvsNetworkConfiguration(virtualNetworkId = testVirtualNetwork.id))
							)
					),
					virtualNetworks = listOf(testVirtualNetwork)
			).index.hostVirtualNetworks[testHost.id] == setOf(testVirtualNetwork.id)
		}
		assertTrue("") {
			OperationalState.fromLists(
					hosts = listOf(testHost),
					hostDyns = listOf(hostDown(testHost)),
					hostCfgs = listOf(
							HostConfiguration(
									id = testHost.id,
									networkConfiguration = listOf(
											OvsNetworkConfiguration(virtualNetworkId = testVirtualNetwork.id))
							)
					),
					virtualNetworks = listOf(testVirtualNetwork)
			).index.hostVirtualNetworks.isEmpty()
		}

	}

	@Test
	fun getVirtualNetworksNeeded() {
		assertTrue("blank state, no network needed") {
			OperationalState.fromLists().index.virtualNetworksNeeded.isEmpty()
		}
		assertTrue("VM has availability expectation but no NIC, no network is needed") {
			OperationalState.fromLists(
					vms = listOf(testVm.copy(
							devices = listOf(),
							expectations = listOf(VirtualMachineAvailabilityExpectation())
					)),
					virtualNetworks = listOf(testVirtualNetwork)
			).index.virtualNetworksNeeded.isEmpty()
		}
		assertTrue("VM has NIC but no availability expectation, no network is needed") {
			OperationalState.fromLists(
					vms = listOf(testVm.copy(
							devices = listOf(
									NetworkDevice(networkId = testVirtualNetwork.id, adapterType = NetworkAdapterType.virtio)
							),
							expectations = listOf()
					)),
					virtualNetworks = listOf(testVirtualNetwork)
			).index.virtualNetworksNeeded.isEmpty()
		}
		assertTrue("VM has availability expectation, network is needed") {
			OperationalState.fromLists(
					vms = listOf(testVm.copy(
							devices = listOf(
									NetworkDevice(networkId = testVirtualNetwork.id, adapterType = NetworkAdapterType.virtio)
							),
							expectations = listOf(VirtualMachineAvailabilityExpectation())
					)),
					virtualNetworks = listOf(testVirtualNetwork)
			).index.virtualNetworksNeeded == setOf(testVirtualNetwork.id)
		}
	}

	@Test
	fun runningVmsByHost() {
		assertTrue("blank state, empty index") {
			OperationalState.fromLists().index.runningVmsToHost.isEmpty()
		}

		assertTrue("single vm on a single host") {
			OperationalState.fromLists(
					hosts = listOf(testHost),
					hostDyns = listOf(hostUp(testHost)),
					vms = listOf(testVm),
					vmDyns = listOf(vmUp(testVm, testHost))
			).index.runningVmsToHost == mapOf(testVm.id to testHost.id)
		}
	}

	@Test
	fun runningVms() {
		assertTrue("blank state, empty index") {
			OperationalState.fromLists().index.runningVms.isEmpty()
		}
		assertTrue("single running vm") {
			OperationalState.fromLists(
					hosts = listOf(testHost),
					hostDyns = listOf(hostUp(testHost)),
					vms = listOf(testVm),
					vmDyns = listOf(vmUp(testVm, testHost))
			).index.runningVms.map { it.stat } == listOf(testVm)
		}
	}

	@Test
	fun hostVirtualNetworkConnections() {
		assertTrue("blank state, empty index") {
			OperationalState.fromLists().index.hostVirtualNetworkConnections.isEmpty()
		}
		assertEquals(
				mapOf(testHost.id to mapOf(testVirtualNetwork.id to setOf(testOtherHost.id))),
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
																OvsGrePort(name = "gre0", remoteAddress = testOtherHost.address)
														)
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
						virtualNetworks = listOf(testVirtualNetwork)
				).index.hostVirtualNetworkConnections)
	}

	@Test
	fun hostsByAddress() {
		assertEquals(
				setOf(testHost.address, testOtherHost.address),
				OperationalState.fromLists(
						hosts = listOf(testHost, testOtherHost)
				).index.hostsByAddress.keys
		)
	}

	@Test
	fun hostsByVirtualNetworks() {
		val host1 = testHost.copy(
				id = randomUUID()
		)
		val host2 = testHost.copy(
				id = randomUUID()
		)
		val testNetwork1 = testVirtualNetwork.copy(
				id = randomUUID()
		)
		val testNetwork2 = testVirtualNetwork.copy(
				id = randomUUID()
		)
		assertEquals(
				mapOf(
						testNetwork1.id to setOf(host1),
						testNetwork2.id to setOf(host1, host2)
				),
				OperationalState.fromLists(
						hosts = listOf(host1, host2),
						hostDyns = listOf(hostUp(host1), hostUp(host2)),
						hostCfgs = listOf(
								HostConfiguration(
										id = host1.id,
										networkConfiguration = listOf(
												OvsNetworkConfiguration(virtualNetworkId = testNetwork1.id),
												OvsNetworkConfiguration(virtualNetworkId = testNetwork2.id)
										)
								),
								HostConfiguration(
										id = host2.id,
										networkConfiguration = listOf(
												OvsNetworkConfiguration(virtualNetworkId = testNetwork2.id)
										)
								)
						),
						virtualNetworks = listOf(testNetwork1, testNetwork2)
				).index.hostsByVirtualNetworks.mapValues { it.value.map { it.stat }.toSet() }
		)
	}
}