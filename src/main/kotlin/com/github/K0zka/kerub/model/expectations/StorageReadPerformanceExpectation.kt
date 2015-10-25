package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.io.IoTune

@JsonTypeName("storage-read-perf")
data class StorageReadPerformanceExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want,
		override val speed: IoTune
                                                                       ) : StoragePerformanceExpectation