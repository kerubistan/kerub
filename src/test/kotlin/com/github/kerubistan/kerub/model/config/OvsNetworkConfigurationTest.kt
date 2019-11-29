package com.github.kerubistan.kerub.model.config

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork

internal class OvsNetworkConfigurationTest : AbstractDataRepresentationTest<OvsNetworkConfiguration>() {
	override val testInstances: Collection<OvsNetworkConfiguration>
		get() = listOf(
				OvsNetworkConfiguration(
						virtualNetworkId = testVirtualNetwork.id,
						ports = listOf(
								OvsGrePort(
										name = "gre0",
										remoteAddress = testHost.address
								),
								OvsDataPort(
										name = "tap1"
								)
						)
				)
		)
	override val clazz: Class<OvsNetworkConfiguration>
		get() = OvsNetworkConfiguration::class.java

}