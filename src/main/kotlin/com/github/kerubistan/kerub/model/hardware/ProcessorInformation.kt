package com.github.kerubistan.kerub.model.hardware

import java.io.Serializable
import java.math.BigDecimal

data class ProcessorInformation(
		val manufacturer: String,
		val flags: List<String> = listOf(),
		val socket: String,
		val version: String,
		val maxSpeedMhz: Int? = null,
		val coreCount: Int? = null,
		val threadCount: Int? = null,
		val voltage: BigDecimal? = null,
		val l1cache: CacheInformation? = null,
		val l2cache: CacheInformation? = null,
		val l3cache: CacheInformation? = null
) : Serializable