package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.utils.validateSize
import java.io.Serializable
import java.math.BigInteger

data class GvinumStorageCapabilityDrive(val name: String, val device: String, val size: BigInteger) : Serializable {
	init {
		size.validateSize("size")
	}
}