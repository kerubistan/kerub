package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.issues.problems.HostProblem

data class VirtualStorageAllocationOnFailingStorageDevice(
		val storageDevice : VirtualStorageDevice,
		val allocation : VirtualStorageAllocation,
		val capability: StorageCapability,
		override val host: Host
) : HostProblem {
	init {
		check(capability.id == allocation.capabilityId) {
			"capability id must be the same as allocation's capability id"
		}
		check(allocation.hostId == host.id) {
			"host id should be the same as the allocation's host id"
		}
	}
}