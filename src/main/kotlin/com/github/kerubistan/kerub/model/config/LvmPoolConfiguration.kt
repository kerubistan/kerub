package com.github.kerubistan.kerub.model.config

import java.math.BigInteger

data class LvmPoolConfiguration(
		val vgName: String,
		val poolName: String,
		val size: BigInteger
) : HostStorageConfiguration