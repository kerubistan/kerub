package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("power-redundancy")
data class PowerRedundancyExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val minPowerCords: Int
) : VirtualMachineExpectation