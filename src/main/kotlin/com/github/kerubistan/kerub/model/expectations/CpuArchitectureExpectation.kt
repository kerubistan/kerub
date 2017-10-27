package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("cpu-architecture")
data class CpuArchitectureExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val cpuArchitecture: String
) : VirtualMachineExpectation