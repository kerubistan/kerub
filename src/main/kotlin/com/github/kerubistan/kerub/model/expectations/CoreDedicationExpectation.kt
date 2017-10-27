package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("core-dedication")
data class CoreDedicationExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want
) : VirtualMachineExpectation