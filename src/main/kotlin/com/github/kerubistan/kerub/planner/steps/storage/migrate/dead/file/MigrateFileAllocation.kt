package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocation

@JsonTypeName("migrate-file-allocation")
data class MigrateFileAllocation(
		override val sourceHost: Host,
		override val targetHost: Host,
		override val virtualStorage: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageFsAllocation,
		override val allocationStep: AbstractCreateVirtualStorage<VirtualStorageFsAllocation, out StorageCapability>,
		override val deAllocationStep: AbstractUnAllocate<*>
) : AbstractMigrateAllocation() {

	init {
		validate()
		check(sourceAllocation.type == allocationStep.allocation.type) {
			"source allocation (${sourceAllocation.type}) must match target allocation" +
					" (${allocationStep.allocation.type})"
		}
	}
}