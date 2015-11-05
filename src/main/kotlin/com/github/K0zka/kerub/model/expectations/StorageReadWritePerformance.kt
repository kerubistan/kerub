package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.io.IoTune

@JsonTypeName("storage-rw-perf")
data class StorageReadWritePerformance constructor(
		override val level: ExpectationLevel,
		override val speed: IoTune
) : StoragePerformanceExpectation