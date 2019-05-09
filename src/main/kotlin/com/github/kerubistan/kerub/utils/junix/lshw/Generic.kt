package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("generic")
data class Generic(
		override val id: String?,
		override val description: String?,
		override val configuration: Map<String, String>?,
		override val children: List<HardwareItem>?
) : HardwareItem