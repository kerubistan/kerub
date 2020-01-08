package com.github.kerubistan.kerub.model.network

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("bond")
data class BondInterface(
		override val name: String,
		override val portSpeedPerSec: Int,
		val devices: List<String>
) : NetworkInterface