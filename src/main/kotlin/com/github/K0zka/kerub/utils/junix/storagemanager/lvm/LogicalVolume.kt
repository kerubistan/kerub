package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import java.math.BigInteger

data class LogicalVolume(
		val id: String,
		val name: String,
		val path: String,
		val size: BigInteger,
		val minRecovery: Int?,
		val maxRecovery: Int?
)