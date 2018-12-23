package com.github.kerubistan.kerub.model.hardware

import java.io.Serializable
import java.math.BigInteger

data class BlockDevice(
		val deviceName: String,
		val storageCapacity : BigInteger
) : Serializable