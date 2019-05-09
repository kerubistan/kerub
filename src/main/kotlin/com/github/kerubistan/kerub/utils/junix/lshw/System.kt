package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("system")
data class System @JsonCreator constructor(
		val product: String? = null,
		val vendor: String? = null,
		val version: String? = null,
		val serial: String? = null,
		override val id: String?,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?
) : HardwareItem