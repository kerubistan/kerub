package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.io.IoTune
import java.util.UUID

JsonTypeName("storage-read-perf")
data class StorageReadPerformanceExpectation constructor(
		override val id: UUID,
		override val level: ExpectationLevel = ExpectationLevel.Want,
		override val speed: IoTune
                                                                       ) : StoragePerformanceExpectation