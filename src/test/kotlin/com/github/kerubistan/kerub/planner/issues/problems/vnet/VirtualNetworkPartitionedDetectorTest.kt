package com.github.kerubistan.kerub.planner.issues.problems.vnet

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.vmUp
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class VirtualNetworkPartitionedDetectorTest : AbstractProblemDetectorVerifications(VirtualNetworkPartitionedDetector) {

	@Test
	fun detect() {
		val firstHost = testHost.copy(
				id = randomUUID()
		)
		val secondHost = testHost.copy(
				id = randomUUID()
		)
		val firstVm = testVm.copy(
				id = randomUUID(),
				devices = listOf(
						NetworkDevice(
								adapterType = NetworkAdapterType.virtio,
								networkId = testVirtualNetwork.id
						)
				)
		)
		val secondVm = testVm.copy(
				id = randomUUID(),
				devices = listOf(
						NetworkDevice(
								adapterType = NetworkAdapterType.virtio,
								networkId = testVirtualNetwork.id
						)
				)
		)
		assertTrue("Two hosts, no vms, no networks, all OK") {
			VirtualNetworkPartitionedDetector.detect(
					Plan(OperationalState.fromLists(
							hosts = listOf(firstHost, secondHost),
							hostDyns = listOf(hostUp(firstHost), hostUp(secondHost)),
							vms = listOf(testVm),
							vmDyns = listOf(vmUp(testVm, firstHost))
					))
			).isEmpty()
		}

		assertTrue("Two hosts, vms running, but no gre - detect problems") {
			VirtualNetworkPartitionedDetector.detect(
					Plan(OperationalState.fromLists(
							hosts = listOf(firstHost, secondHost),
							hostDyns = listOf(hostUp(firstHost), hostUp(secondHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = firstHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(
																			name = firstVm.idStr
																	)
															)
													)
											)
									),
									HostConfiguration(
											id = secondHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(
																			name = secondVm.idStr
																	)
															)
													)
											)
									)
							),
							vms = listOf(testVm),
							vmDyns = listOf(vmUp(firstVm, firstHost), vmUp(secondVm, secondHost)),
							virtualNetworks = listOf(testVirtualNetwork)
					))
			).isNotEmpty()
		}

		assertTrue("Two hosts, vms running, but no gre from one side - still detect a problem") {
			VirtualNetworkPartitionedDetector.detect(
					Plan(OperationalState.fromLists(
							hosts = listOf(firstHost, secondHost),
							hostDyns = listOf(hostUp(firstHost), hostUp(secondHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = firstHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(
																			name = firstVm.idStr
																	),
																	OvsGrePort(
																			name = "gre0",
																			remoteAddress = secondHost.address
																	)
															)
													)
											)
									),
									HostConfiguration(
											id = secondHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(
																			name = secondVm.idStr
																	)
															)
													)
											)
									)
							),
							vms = listOf(testVm),
							vmDyns = listOf(vmUp(firstVm, firstHost), vmUp(secondVm, secondHost)),
							virtualNetworks = listOf(testVirtualNetwork)
					))
			).isNotEmpty()
		}

		assertTrue("Two hosts, vms running, gre from both one sides - no problem") {
			VirtualNetworkPartitionedDetector.detect(
					Plan(OperationalState.fromLists(
							hosts = listOf(firstHost, secondHost),
							hostDyns = listOf(hostUp(firstHost), hostUp(secondHost)),
							hostCfgs = listOf(
									HostConfiguration(
											id = firstHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(
																			name = firstVm.idStr
																	),
																	OvsGrePort(
																			name = "gre0",
																			remoteAddress = secondHost.address
																	)
															)
													)
											)
									),
									HostConfiguration(
											id = secondHost.id,
											networkConfiguration = listOf(
													OvsNetworkConfiguration(
															virtualNetworkId = testVirtualNetwork.id,
															ports = listOf(
																	OvsDataPort(
																			name = secondVm.idStr
																	),
																	OvsGrePort(
																			name = "gre0",
																			remoteAddress = firstHost.address
																	)
															)
													)
											)
									)
							),
							vms = listOf(testVm),
							vmDyns = listOf(vmUp(firstVm, firstHost), vmUp(secondVm, secondHost)),
							virtualNetworks = listOf(testVirtualNetwork)
					))
			).isEmpty()
		}

	}
}