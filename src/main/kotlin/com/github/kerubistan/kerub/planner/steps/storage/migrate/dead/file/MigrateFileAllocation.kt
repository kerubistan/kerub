package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocation

data class MigrateFileAllocation(
		override val sourceHost: Host,
		override val targetHost: Host,
		override val virtualStorage: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageFsAllocation,
		override val allocationStep: AbstractCreateVirtualStorage<VirtualStorageAllocation, StorageCapability>,
		override val deAllocationStep: AbstractUnAllocate<*>
) : AbstractMigrateAllocation() {

	init {
		validate()
	}
}