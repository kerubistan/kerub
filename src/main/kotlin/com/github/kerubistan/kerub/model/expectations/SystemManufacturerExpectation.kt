package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("host-system-manufacturer")
data class SystemManufacturerExpectation @JsonCreator constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val manufacturer: String
) : VirtualMachineExpectation