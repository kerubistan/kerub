package com.github.kerubistan.kerub.utils.junix.lsblk

import java.math.BigInteger

data class BlockDeviceInfo(
		val name: String,
		val type: String,
		val rotational: Boolean,
		val readOnly: Boolean,
		val readAhead: BigInteger,
		val removable: Boolean = false,
		val minIo: BigInteger,
		val optIo: BigInteger)