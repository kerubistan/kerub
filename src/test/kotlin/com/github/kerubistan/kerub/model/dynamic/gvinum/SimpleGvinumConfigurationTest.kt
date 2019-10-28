package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest

internal class SimpleGvinumConfigurationTest : AbstractDataRepresentationTest<SimpleGvinumConfiguration>() {
	override val testInstances = listOf(
			SimpleGvinumConfiguration(
					diskName = "test-disk"
			)
	)
	override val clazz = SimpleGvinumConfiguration::class.java

}