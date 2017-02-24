package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("core-dedication")
data class CoreDedicationExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want
) : VirtualMachineExpectation