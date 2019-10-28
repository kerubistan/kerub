package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import io.github.kerubistan.kroki.time.now
import java.util.UUID.randomUUID

internal class VirtualNetworkDynamicTest : AbstractDataRepresentationTest<VirtualNetworkDynamic>() {
	override val testInstances = listOf(
			VirtualNetworkDynamic(
					id = randomUUID(),
					lastUpdated = now()
			)
	)
	override val clazz = VirtualNetworkDynamic::class.java
}