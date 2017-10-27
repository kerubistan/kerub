package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.io.IoTune

@JsonTypeName("storage-write-perf")
data class StorageWritePerformanceExpectation constructor(
		override val level: ExpectationLevel,
		override val speed: IoTune
) : StoragePerformanceExpectation