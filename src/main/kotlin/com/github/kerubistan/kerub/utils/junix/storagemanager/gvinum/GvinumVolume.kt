package com.github.kerubistan.kerub.utils.junix.storagemanager.gvinum

import java.io.Serializable
import java.math.BigInteger

data class GvinumVolume(
		val name: String,
		val up: Boolean,
		val size: BigInteger
) : Serializable