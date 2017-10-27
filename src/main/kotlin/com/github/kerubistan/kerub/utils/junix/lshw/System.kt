package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("system")
data class System(
		val product: String,
		val vendor: String,
		val version: String,
		val serial: String,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?
) : HardwareItem