package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.IOCost
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage

abstract class AbstractCreateLv : AbstractCreateVirtualStorage<VirtualStorageLvmAllocation, LvmStorageCapability> {

	@get:JsonIgnore
	val volumeGroupName: String
		get() = capability.volumeGroupName

	@JsonIgnore
	override fun getCost(): List<Cost> = listOf(
			IOCost(2048, host)
	)

	@get:JsonIgnore
	override val format: VirtualDiskFormat
		get() = VirtualDiskFormat.raw
}