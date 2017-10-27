package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("cache-size")
data class CacheSizeExpectation @JsonCreator constructor(
		override val level: ExpectationLevel,
		val minL1: Long
) : VirtualMachineExpectation