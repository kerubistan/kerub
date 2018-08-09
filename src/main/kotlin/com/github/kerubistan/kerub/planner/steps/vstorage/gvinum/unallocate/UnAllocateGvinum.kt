package com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.unallocate

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.planner.steps.base.UnAllocate

data class UnAllocateGvinum(
		override val vstorage: VirtualStorageDevice,
		override val allocation: VirtualStorageGvinumAllocation,
		override val host: Host) : UnAllocate<VirtualStorageGvinumAllocation>() {
	init {
		check(allocation.hostId == host.id) {
			"allocation host id (${allocation.hostId} must be equal to the host id (${host.id}))"
		}
	}

}