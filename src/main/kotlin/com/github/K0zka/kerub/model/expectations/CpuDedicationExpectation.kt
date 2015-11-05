package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("cpu-dedication")
data class CpuDedicationExpectation constructor(
		override val level: ExpectationLevel,
		val dedicatedVCpus: List<Int>
) : Expectation