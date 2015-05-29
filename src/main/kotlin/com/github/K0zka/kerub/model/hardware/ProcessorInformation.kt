package com.github.K0zka.kerub.model.hardware

import java.io.Serializable
import java.math.BigDecimal

data class ProcessorInformation(
		val manufacturer: String,
		val flags: List<String>,
		val socket: String,
		val version: String,
		val maxSpeedMhz: Int?,
		val coreCount: Int,
		val threadCount: Int,
		val voltage: BigDecimal?,
		val l1cache: CacheInformation? = null,
		val l2cache: CacheInformation? = null,
		val l3cache: CacheInformation? = null
                               ) : Serializable