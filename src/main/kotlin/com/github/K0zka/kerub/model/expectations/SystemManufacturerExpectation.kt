package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

JsonTypeName("host-system-manufacturer")
data class SystemManufacturerExpectation @JsonCreator constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want
                                                                 ) : Expectation