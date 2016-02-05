package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("ecc-memory")
data class EccMemoryExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker
) : VirtualMachineExpectation