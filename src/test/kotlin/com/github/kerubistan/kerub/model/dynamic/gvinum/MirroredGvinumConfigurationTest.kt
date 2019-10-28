package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest

internal class MirroredGvinumConfigurationTest : AbstractDataRepresentationTest<MirroredGvinumConfiguration>() {
	override val testInstances = listOf(
			MirroredGvinumConfiguration(disks = listOf("test-disk-1", "test-disk-2"))
	)
	override val clazz = MirroredGvinumConfiguration::class.java

}