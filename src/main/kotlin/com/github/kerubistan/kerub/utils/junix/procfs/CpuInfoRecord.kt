package com.github.kerubistan.kerub.utils.junix.procfs

import java.io.Serializable
import java.math.BigInteger

data class CpuInfoRecord(
		val nr: Int,
		val vendorId: String,
		val cpuFamily: Int,
		val modelId: Int,
		val modelName: String,
		val mhz: Float,
		val cacheSize: BigInteger,
		val flags: List<String>
) : Serializable