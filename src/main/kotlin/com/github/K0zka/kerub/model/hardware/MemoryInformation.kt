package com.github.K0zka.kerub.model.hardware

import java.io.Serializable
import java.math.BigInteger

data class MemoryInformation(
		val size: BigInteger,
		val type: String,
		val formFactor: String,
		val locator: String,
		val bankLocator: String,
		val speedMhz: Int?,
		val manufacturer: String,
		val serialNumber: String,
		val partNumber: String,
		val configuredSpeedMhz: Int?) : Serializable