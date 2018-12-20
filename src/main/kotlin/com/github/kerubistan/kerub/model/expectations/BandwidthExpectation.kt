package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("bandwidth")
data class BandwidthExpectation @JsonCreator constructor(
		val bandwidthBitPerSecond: Long,
		override val level: ExpectationLevel = ExpectationLevel.Want
) : VirtualNetworkExpectation {
	init {
		check(bandwidthBitPerSecond > 0) {
			"bandwidth ($bandwidthBitPerSecond bits/sec) must be greater than 0"
		}
	}
}