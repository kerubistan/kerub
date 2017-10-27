package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("cpu-clock-freq")
data class ClockFrequencyExpectation @JsonCreator constructor(
		override val level: ExpectationLevel,
		val minimalClockFrequency: Int
) : VirtualMachineExpectation