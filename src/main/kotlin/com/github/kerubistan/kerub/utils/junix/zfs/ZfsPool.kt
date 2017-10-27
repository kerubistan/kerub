package com.github.kerubistan.kerub.utils.junix.zfs

import java.io.Serializable
import java.math.BigInteger

data class ZfsPool(
		val name: String,
		val id: String,
		val allocated: BigInteger,
		val capacity: Double,
		val dedupratio: Double,
		val readOnly: Boolean
) : Serializable