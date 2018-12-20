package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("latency")
data class LatencyExpectation @JsonCreator constructor(
		val latencyMicroSec: Int,
		override val level: ExpectationLevel = ExpectationLevel.Want
) : VirtualNetworkExpectation {
	init {
		check(latencyMicroSec > 0) { "latency ($latencyMicroSec) must be greater than 0" }
	}
}