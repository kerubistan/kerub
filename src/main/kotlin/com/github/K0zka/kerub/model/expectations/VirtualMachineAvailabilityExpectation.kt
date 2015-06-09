package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import java.util.UUID
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("cpu-architecture")
data class VirtualMachineAvailabilityExpectation @JsonCreator constructorconstructor(
		override val id: UUID,
		override val level : ExpectationLevel = ExpectationLevel.Want,
		val up: Boolean = true
                                                               ) : Expectation