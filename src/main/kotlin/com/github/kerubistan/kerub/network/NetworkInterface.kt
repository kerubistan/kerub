package com.github.kerubistan.kerub.network

import java.io.Serializable

data class NetworkInterface (
		val name: String,
		val portSpeedPerSec: Int
) : Serializable
