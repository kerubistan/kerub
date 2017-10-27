package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import java.math.BigInteger

data class LogicalVolume(
		val id: String,
		val name: String,
		val path: String,
		val size: BigInteger,
		val layout: String,
		val dataPercent: Double?,
		val minRecovery: Int?,
		val maxRecovery: Int?
)