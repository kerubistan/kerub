package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import java.util.UUID.randomUUID

class DataLossAlertTest : AbstractDataRepresentationTest<DataLossAlert>() {
	override val testInstances: Collection<DataLossAlert>
		get() = listOf(
				DataLossAlert(
						id = randomUUID(),
						open = true,
						resolved = null,
						storageId = randomUUID()
				)
		)
	override val clazz: Class<DataLossAlert>
		get() = DataLossAlert::class.java

}