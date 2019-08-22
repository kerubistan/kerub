package com.github.kerubistan.kerub.model.dynamic.lvm

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamicItem
import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger

@JsonTypeName("pv")
data class LvmStorageDeviceDynamicItem(
		override val name: String, override val freeCapacity: BigInteger
) : CompositeStorageDeviceDynamicItem() {
	init {
		freeCapacity.validateSize("freeCapacity")
	}
}