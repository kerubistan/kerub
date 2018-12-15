package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger

/**
 * Concatenated gvinum configuration
 */
@JsonTypeName("concatenated-configuration")
data class ConcatenatedGvinumConfiguration(
		val disks: Map<String, BigInteger>
) : GvinumConfiguration {
	override val diskNames: Collection<String>
		get() = disks.keys

	init {
		check(disks.isNotEmpty()) { "Empty configuration is invalid" }
	}
}