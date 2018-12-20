package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("network-redundancy")
data class NetworkRedundancyExpectation @JsonCreator constructor(
		val minRedundancy: Short,
		override val level: ExpectationLevel
) : VirtualNetworkExpectation