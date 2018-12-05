package com.github.kerubistan.kerub.planner.steps.vstorage.fs.truncate

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.utils.update

data class TruncateImage(override val host: Host,
						 override val capability: FsStorageCapability,
					override val disk: VirtualStorageDevice,
					override val allocation: VirtualStorageFsAllocation)
	: AbstractCreateVirtualStorage<VirtualStorageFsAllocation, FsStorageCapability> {

	init {
		check(allocation.type == VirtualDiskFormat.raw) {
			"truncate can do only raw allocations, not ${allocation.type}"
		}
		check(allocation.hostId == host.id) {
			"the host id of the allocation (${allocation.hostId}) must match the host's id (${host.id})"
		}
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage.update(disk.id) {
				it.copy(
						dynamic =
						it.dynamic?.copy(
								allocations = it.dynamic.allocations + allocation
						) ?: VirtualStorageDeviceDynamic(
								id = disk.id,
								allocations = listOf(allocation)
						)
				)
			}
	)

}