package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.utils.validateSize
import java.io.Serializable
import java.math.BigInteger

data class CompositeStorageDeviceDynamicItem(
		val name : String,
		val freeCapacity: BigInteger
) : Serializable {
	init {
		freeCapacity.validateSize("freeCapacity")
	}
}