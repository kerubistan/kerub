package com.github.kerubistan.kerub.model.hardware

import java.io.Serializable

/**
 * Provides information about a cache of a CPU.
 */
data class CacheInformation(
		val socket: String,
		val operation: String,
		val size: Int,
		val speedNs: Int?,
		val errorCorrection: String
) : Serializable
