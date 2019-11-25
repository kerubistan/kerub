package com.github.kerubistan.kerub.model.config

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost

internal class HostConfigurationTest : AbstractDataRepresentationTest<HostConfiguration>() {
	override val testInstances = listOf(
			HostConfiguration(
					id = testHost.id
			)
	)
	override val clazz = HostConfiguration::class.java

}