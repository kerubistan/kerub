package com.github.kerubistan.kerub.network

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("simple")
data class SimpleInterface(
		override val name: String,
		override val portSpeedPerSec: Int,
		val device : String
) : NetworkInterface