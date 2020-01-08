package com.github.kerubistan.kerub.utils.junix.procfs

data class SlaveInterface(
		val name: String,
		val speedMbps: Int?,
		val duplex: Boolean?,
		val hardwareAddress: String
)