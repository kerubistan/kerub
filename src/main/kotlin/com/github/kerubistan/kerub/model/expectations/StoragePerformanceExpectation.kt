package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.io.IoTune

@JsonTypeName("storage-performance")
interface StoragePerformanceExpectation : VirtualStorageExpectation {
	override val level: ExpectationLevel
	val speed: IoTune
}
