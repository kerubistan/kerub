package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("network")
data class NetworkInterface @JsonCreator constructor(
		override val id: String?,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?,
		val serial: String?,
		@JsonProperty("logicalname")
		val logicalName: String?,
		val capacity: Int?
) : HardwareItem