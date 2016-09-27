package com.github.K0zka.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger

/**
 * Concatenated gvinum configuration
 */
@JsonTypeName("concatenated-configuration")
data class ConcatenatedGvinumConfiguration(
		val disks: Map<String, BigInteger>
) : GvinumConfiguration