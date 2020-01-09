package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.time.now
import java.util.UUID.randomUUID

internal class NetworkLinkDownAlertTest : AbstractDataRepresentationTest<NetworkLinkDownAlert>() {
	override val testInstances: Collection<NetworkLinkDownAlert>
		get() = listOf(
				NetworkLinkDownAlert(
						id = randomUUID(),
						open = true,
						resolved = null,
						created = now(),
						hostId = testHost.id
				)
		)
	override val clazz = NetworkLinkDownAlert::class.java
}