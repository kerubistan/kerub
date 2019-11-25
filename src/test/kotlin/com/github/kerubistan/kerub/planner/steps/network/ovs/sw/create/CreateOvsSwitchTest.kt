package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove.RemoveOvsSwitch
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CreateOvsSwitchTest : OperationalStepVerifications() {
	override val step = CreateOvsSwitch(host = testHost, network = testVirtualNetwork)

	@Test
	fun isInverseOf() {
		assertTrue("the inverse") {
			step.isInverseOf(RemoveOvsSwitch(host = testHost, virtualNetwork = testVirtualNetwork))
		}
		assertFalse("some random step") {
			step.isInverseOf(mock())
		}
		assertFalse("host not the same, not inverse") {
			step.isInverseOf(RemoveOvsSwitch(host = testOtherHost, virtualNetwork = testVirtualNetwork))
		}
		assertFalse("host not the same, not inverse") {
			step.isInverseOf(
					RemoveOvsSwitch(host = testHost, virtualNetwork = testVirtualNetwork.copy(id = randomUUID()))
			)
		}
	}

	@Test
	fun take() {
		val state = CreateOvsSwitch(host = testHost, network = testVirtualNetwork).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostDyns = listOf(hostUp(testHost)),
						virtualNetworks = listOf(testVirtualNetwork)
				)
		)
		assertTrue {
			state.hosts.getValue(testHost.id).config!!.networkConfiguration.contains(
					OvsNetworkConfiguration(virtualNetworkId = testVirtualNetwork.id)
			)
		}
	}

	@Test
	fun otherReservations() {
		assertTrue {
			step.reservations().contains(UseHostReservation(host = testHost))
		}
	}
}