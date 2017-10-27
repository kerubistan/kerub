package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("ram-clock-freq")
data class MemoryClockFrequencyExpectation @JsonCreator constructor(
		override val level: ExpectationLevel,
		val min: Int
) : VirtualMachineExpectation

