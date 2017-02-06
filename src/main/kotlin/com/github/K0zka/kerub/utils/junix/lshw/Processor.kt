package com.github.K0zka.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("processor")
data class Processor(
		val handle: String,
		val product: String,
		val vendor: String,
		val physid: String,
		val businfo: String,
		val capabilities: Map<String, Any> = mapOf(),
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?
) : HardwareItem