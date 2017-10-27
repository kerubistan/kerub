package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("host-chassis-manufacturer")
data class ChassisManufacturerExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val manufacturer: String
) : VirtualMachineExpectation, VirtualStorageExpectation