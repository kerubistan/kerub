package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.time.now
import java.util.UUID.randomUUID

internal class HostOverheatingAlertTest : AbstractDataRepresentationTest<HostOverheatingAlert>() {
	override val testInstances: Collection<HostOverheatingAlert>
		get() = listOf(
				HostOverheatingAlert(
						id = randomUUID(),
						hostId = testHost.id,
						open = true,
						created = now(),
						resolved = null
				)
		)
	override val clazz = HostOverheatingAlert::class.java
}