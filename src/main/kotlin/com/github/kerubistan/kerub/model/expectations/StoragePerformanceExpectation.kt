package com.github.kerubistan.kerub.model.expectations

import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.io.IoTune

interface StoragePerformanceExpectation : VirtualStorageExpectation {
	override val level: ExpectationLevel
	val speed: IoTune
}
