package com.github.kerubistan.kerub.planner.steps.network.ovs.port.create

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class CreateOvsPortTest : OperationalStepVerifications() {
	override val step = CreateOvsPort(
			host = testHost,
			virtualNetwork = testVirtualNetwork,
			portName = "test-port-1"
	)

	@Test
	fun take() {
		val state = CreateOvsPort(
				host = testHost,
				virtualNetwork = testVirtualNetwork,
				portName = "test-port-1"
		).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostDyns = listOf(hostUp(testHost)),
						hostCfgs = listOf(
								HostConfiguration(
										id = testHost.id,
										networkConfiguration = listOf(
												OvsNetworkConfiguration(
														virtualNetworkId = testVirtualNetwork.id,
														ports = listOf()
												)
										)
								)
						),
						virtualNetworks = listOf(testVirtualNetwork)
				)
		)
		assertTrue {
			state.hosts.getValue(testHost.id).config!!.networkConfiguration.single() ==
					OvsNetworkConfiguration(
							virtualNetworkId = testVirtualNetwork.id,
							ports = listOf(OvsDataPort("test-port-1"))
					)
		}
	}

	@Test
	fun otherReservations() {
		assertTrue { step.reservations().contains(UseHostReservation(host = testHost)) }
	}
}