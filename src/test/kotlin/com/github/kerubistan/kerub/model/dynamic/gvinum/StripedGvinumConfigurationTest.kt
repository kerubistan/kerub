package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest

internal class StripedGvinumConfigurationTest : AbstractDataRepresentationTest<StripedGvinumConfiguration>() {
	override val testInstances = listOf(
			StripedGvinumConfiguration(disks = listOf("test-disk-1", "test-disk-2", "test-disk-3"))
	)
	override val clazz = StripedGvinumConfiguration::class.java
}