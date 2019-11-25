package com.github.kerubistan.kerub.model.config

import com.github.kerubistan.kerub.testVirtualNetwork
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class OvsNetworkConfigurationIndexTest {

	@Test
	fun getPortNames() {
		assertEquals(
				setOf("test-port"),
				OvsNetworkConfiguration(
						virtualNetworkId = testVirtualNetwork.id,
						ports = listOf(
								OvsDataPort(
										name = "test-port"
								)
						)
				).index.portNames
		)
	}
}