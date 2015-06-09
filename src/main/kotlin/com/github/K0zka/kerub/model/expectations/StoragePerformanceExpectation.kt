package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.io.IoTune
import java.util.UUID

interface StoragePerformanceExpectation : Expectation {
	override val id: UUID
	override val level: ExpectationLevel
	val speed: IoTune
}
