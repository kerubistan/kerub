package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create.CreateOvsSwitch
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class RemoveOvsSwitchTest : OperationalStepVerifications() {
	override val step = RemoveOvsSwitch(
			host = testHost,
			virtualNetwork = testVirtualNetwork
	)


	@Test
	fun isInverseOf() {
		assertTrue("should be inverse") {
			step.isInverseOf(CreateOvsSwitch(host = testHost, network = testVirtualNetwork))
		}
		assertFalse("random something is not inverse") {
			step.isInverseOf(mock())
		}
	}


	@Test
	fun take() {
		assertTrue("remove single switch") {
			val state = RemoveOvsSwitch(
					host = testHost,
					virtualNetwork = testVirtualNetwork
			).take(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(HostConfiguration(
									id = testHost.id,
									networkConfiguration = listOf(
											OvsNetworkConfiguration(
													virtualNetworkId = testVirtualNetwork.id,
													ports = listOf()
											)
									)
							))
					)
			)

			state.hosts.getValue(testHost.id).config!!.networkConfiguration.isEmpty()
		}

		assertTrue("leave the net that is not removed") {
			val otherNetwork = testVirtualNetwork.copy(
					id = randomUUID()
			)
			val state = RemoveOvsSwitch(
					host = testHost,
					virtualNetwork = testVirtualNetwork
			).take(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(hostUp(testHost)),
							hostCfgs = listOf(HostConfiguration(
									id = testHost.id,
									networkConfiguration = listOf(
											OvsNetworkConfiguration(
													virtualNetworkId = testVirtualNetwork.id,
													ports = listOf()
											),
											OvsNetworkConfiguration(
													virtualNetworkId = otherNetwork.id,
													ports = listOf()
											)
									)
							))
					)
			)

			state.hosts.getValue(testHost.id).config!!.networkConfiguration == listOf(
					OvsNetworkConfiguration(
							virtualNetworkId = otherNetwork.id,
							ports = listOf()
					)
			)
		}
	}

	@Test
	fun otherReservations() {
		assertTrue(step.reservations().contains(UseHostReservation(testHost)))
	}
}