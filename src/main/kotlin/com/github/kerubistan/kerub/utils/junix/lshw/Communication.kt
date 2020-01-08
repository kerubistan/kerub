package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("communication")
data class Communication(
		override val id: String?,
		override val description: String?,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?
) : HardwareItem