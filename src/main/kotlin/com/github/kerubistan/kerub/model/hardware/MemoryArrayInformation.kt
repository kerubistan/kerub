package com.github.kerubistan.kerub.model.hardware

import java.io.Serializable
import java.math.BigInteger

data class MemoryArrayInformation(
		val maxCapacity: BigInteger?,
		val errorCorrection: String?,
		val location: String?
) : Serializable