package com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate

@JsonTypeName("unallocate-fs")
data class UnAllocateFs(override val vstorage: VirtualStorageDevice,
						override val allocation: VirtualStorageFsAllocation,
						override val host: Host) : AbstractUnAllocate<VirtualStorageFsAllocation>() {
	init {
		check(allocation.hostId == host.id) {
			"allocation host id (${allocation.hostId} must be equal to the host id (${host.id}))"
		}
	}

}