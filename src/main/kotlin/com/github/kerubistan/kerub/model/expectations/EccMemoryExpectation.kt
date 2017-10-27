package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("ecc-memory")
data class EccMemoryExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker
) : VirtualMachineExpectation