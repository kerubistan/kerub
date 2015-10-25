package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("cpu-architecture")
data class CpuArchitectureExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val cpuArchitecture: String
                                                              ) : Expectation