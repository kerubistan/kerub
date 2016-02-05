package com.github.K0zka.kerub.utils.junix.bridgectl

data class Bridge(
		val name: String,
		val id: String,
		val stpEnabled: Boolean,
		val ifaces: List<String>
)