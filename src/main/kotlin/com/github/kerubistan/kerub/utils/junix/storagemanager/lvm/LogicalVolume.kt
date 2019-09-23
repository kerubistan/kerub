package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import java.math.BigInteger

data class LogicalVolume(
		val id: String,
		val name: String,
		val volumeGroupName: String,
		val path: String,
		val size: BigInteger,
		val layout: List<String>,
		val dataPercent: Double?,
		val minRecovery: Int?,
		val maxRecovery: Int?
)