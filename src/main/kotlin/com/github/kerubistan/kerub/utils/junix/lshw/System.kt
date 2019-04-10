package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("system")
data class System(
		val product: String? = null,
		val vendor: String? = null,
		val version: String? = null,
		val serial: String? = null,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?
) : HardwareItem