package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("processor")
data class Processor @JsonCreator constructor(
		val handle: String?,
		val product: String?,
		val vendor: String?,
		val physid: String,
		val businfo: String,
		val capabilities: Map<String, Any> = mapOf(),
		override val id: String?,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?
) : HardwareItem