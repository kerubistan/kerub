package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

JsonTypeName("host-chassis-manufacturer")
data class ChassisManufacturerExpectation constructor(
		override val id: UUID,
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val manufacturer: String
                                                                    ) : Expectation