package com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate

@JsonTypeName("unallocate-lv")
data class UnAllocateLv(override val vstorage: VirtualStorageDevice,
						override val allocation: VirtualStorageLvmAllocation,
						override val host: Host) : AbstractUnAllocate<VirtualStorageLvmAllocation>() {
	init {
		check(allocation.hostId == host.id) {
			"allocation host id (${allocation.hostId} must be equal to the host id (${host.id}))"
		}
	}
}