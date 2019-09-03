package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamicItem
import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger

@JsonTypeName("disk")
data class GvinumStorageDeviceDynamicItem(override val name: String, override val freeCapacity: BigInteger) : CompositeStorageDeviceDynamicItem {
	init {
		freeCapacity.validateSize("freeCapacity")
	}
}