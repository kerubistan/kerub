package com.github.kerubistan.kerub.model.config

import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HostConfigurationIndexTest {

	@Test
	fun getOvsNetworkConfigurations() {
		assertEquals(
				mapOf(testVirtualNetwork.id to
						OvsNetworkConfiguration(
								virtualNetworkId = testVirtualNetwork.id,
								ports = listOf()
						)
				),
				HostConfiguration(
						id = testHost.id,
						networkConfiguration = listOf(
								OvsNetworkConfiguration(
										virtualNetworkId = testVirtualNetwork.id,
										ports = listOf()
								)
						)
				).index.ovsNetworkConfigurations
		)
	}
}