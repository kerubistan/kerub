package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("memory")
data class Memory(
		val size: Long,
		val units: String,

		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?,
		override val description: String?
) : HardwareItem