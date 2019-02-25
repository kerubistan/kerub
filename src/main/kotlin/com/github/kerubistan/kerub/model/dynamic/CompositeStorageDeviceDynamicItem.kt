package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger

data class CompositeStorageDeviceDynamicItem (val name : String, val freeCapacity: BigInteger) {
	init {
		freeCapacity.validateSize("freeCapacity")
	}
}