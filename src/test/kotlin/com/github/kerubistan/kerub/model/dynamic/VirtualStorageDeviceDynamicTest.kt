package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testDisk

internal class VirtualStorageDeviceDynamicTest : AbstractDataRepresentationTest<VirtualStorageDeviceDynamic>() {
	override val testInstances = listOf(
			VirtualStorageDeviceDynamic(
					id = testDisk.id,
					allocations = listOf()
			)
	)
	override val clazz = VirtualStorageDeviceDynamic::class.java
}