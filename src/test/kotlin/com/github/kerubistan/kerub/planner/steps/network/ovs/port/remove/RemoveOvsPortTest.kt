package com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.planner.steps.network.ovs.port.create.CreateOvsPort
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class RemoveOvsPortTest : OperationalStepVerifications() {
	override val step = RemoveOvsPort(testHost, virtualNetwork = testVirtualNetwork, portName = "TEST")

	@Test
	@Override
	fun otherReservations() {
		assertTrue {
			step.reservations().contains(UseHostReservation(testHost))
		}
	}

	@Test
	fun take() {
		val state = RemoveOvsPort(testHost, virtualNetwork = testVirtualNetwork, portName = "TEST").take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostDyns = listOf(hostUp(testHost)),
						hostCfgs = listOf(HostConfiguration(
								id = testHost.id,
								networkConfiguration = listOf(
										OvsNetworkConfiguration(
												virtualNetworkId = testVirtualNetwork.id,
												ports = listOf(
														OvsDataPort("TEST")
												)
										)
								)
						))
				)
		)
		assertTrue {
			(state.hosts.getValue(testHost.id).config!!.networkConfiguration
					.single { it.virtualNetworkId == testVirtualNetwork.id } as OvsNetworkConfiguration).ports.isEmpty()
		}
	}

	@Test
	fun isInverseOf() {
		assertTrue() {
			RemoveOvsPort(testHost, virtualNetwork = testVirtualNetwork, portName = "TEST").isInverseOf(
					CreateOvsPort(testHost, virtualNetwork = testVirtualNetwork, portName = "TEST")
			)

		}

	}
}