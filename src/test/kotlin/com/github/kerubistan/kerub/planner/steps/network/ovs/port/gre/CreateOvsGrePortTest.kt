package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualNetwork
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

internal class CreateOvsGrePortTest : OperationalStepVerifications() {
	override val step = CreateOvsGrePort(
			firstHost = testHost,
			secondHost = testOtherHost,
			virtualNetwork = testVirtualNetwork,
			name = "gre0"
	)

	@Test
	fun validate() {
		assertThrows<IllegalStateException>("Same host should not be accepted") {
			CreateOvsGrePort(
					firstHost = testHost,
					secondHost = testHost,
					virtualNetwork = testVirtualNetwork,
					name = "gre0"
			)
		}
	}

	@Test
	fun take() {
		val state = CreateOvsGrePort(
				firstHost = testHost,
				secondHost = testOtherHost,
				virtualNetwork = testVirtualNetwork,
				name = "gre0"
		).take(
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
						virtualNetworks = listOf(testVirtualNetwork)
				)
		)

		assertTrue {
			OvsGrePort(
					name = "gre0",
					remoteAddress = testOtherHost.address
			) in
					(state.hosts.getValue(testHost.id).config!!.networkConfiguration.single() as OvsNetworkConfiguration).ports
		}

	}
}