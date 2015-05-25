package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.io.IoTune
import java.util.UUID

JsonTypeName("storage-write-perf")
data class StorageWritePerformanceExpectation [JsonCreator] (
		override val id : UUID,
        override val level : ExpectationLevel,
        override val speed : IoTune
                                                            ) : StoragePerformanceExpectation