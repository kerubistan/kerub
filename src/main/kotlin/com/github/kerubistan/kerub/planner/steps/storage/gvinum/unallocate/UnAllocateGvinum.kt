package com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate

@JsonTypeName("unallocate-gvinum")
data class UnAllocateGvinum(
		override val vstorage: VirtualStorageDevice,
		override val allocation: VirtualStorageGvinumAllocation,
		override val host: Host
) : AbstractUnAllocate<VirtualStorageGvinumAllocation>() {
	init {
		check(host.capabilities?.os == OperatingSystem.BSD) {
			"Only FreeBSD supports gvinum"
		}
		check(allocation.hostId == host.id) {
			"allocation host id (${allocation.hostId} must be equal to the host id (${host.id}))"
		}
	}

}