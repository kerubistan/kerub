package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("bus")
data class Bus @JsonCreator constructor(
		val handle: String,
		val product: String,
		val vendor: String,
		override val description: String,
		@JsonProperty("configuration")
		override val configuration: Map<String, String>?,
		@JsonProperty("children")
		override val children: List<HardwareItem>?
) : HardwareItem