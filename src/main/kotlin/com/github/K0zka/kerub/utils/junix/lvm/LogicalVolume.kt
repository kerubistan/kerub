package com.github.K0zka.kerub.utils.junix.lvm

import java.math.BigInteger
import java.util.UUID

data class LogicalVolume(
		val id: UUID,
		val name: String,
		val path: String,
		val size: BigInteger,
		val minRecovery: Int,
		val maxRecovery: Int
)