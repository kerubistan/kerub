package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.IOCost
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage

abstract class AbstractCreateLv : AbstractCreateVirtualStorage<VirtualStorageLvmAllocation, LvmStorageCapability> {
	val volumeGroupName: String
		get() = capability.volumeGroupName

	override fun getCost(): List<Cost> = listOf(
			IOCost(2048, host)
	)
}