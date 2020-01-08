package com.github.kerubistan.kerub.utils.junix.smartmontools

import java.math.BigInteger

data class StorageDevice(
		val modelFamily: String,
		val deviceModel: String,
		val serialNumber: String,
		val firmwareVersion: String,
		val userCapacity: BigInteger,
		val rotationRate: Int?,
		val sataVersion: String?
)