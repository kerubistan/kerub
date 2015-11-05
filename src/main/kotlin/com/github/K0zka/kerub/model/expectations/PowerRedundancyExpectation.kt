package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("power-redundancy")
data class PowerRedundancyExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val minPowerCords: Int
) : Expectation