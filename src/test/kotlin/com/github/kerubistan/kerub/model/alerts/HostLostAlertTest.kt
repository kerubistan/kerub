package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.time.now
import java.util.UUID.randomUUID

class HostLostAlertTest : AbstractDataRepresentationTest<HostLostAlert>() {
	override val testInstances: Collection<HostLostAlert>
		get() = listOf(
				HostLostAlert(
						id = randomUUID(),
						created = now(),
						open = true,
						resolved = null,
						hostId = testHost.id
				)
		)
	override val clazz = HostLostAlert::class.java
}