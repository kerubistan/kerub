package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.utils.update

abstract class AbstractCreateImage : AbstractCreateVirtualStorage<VirtualStorageFsAllocation, FsStorageCapability> {

	@get:JsonIgnore
	val path
		get() = capability.mountPoint

	/*
	 * TODO: add costs here:
	 * - overallocation and underallocation of
	 * - bandwidth and storage capacity
	 */
	override fun take(state: OperationalState): OperationalState =
			state.copy(
					vStorage = state.vStorage.update(disk.id) {
						it.copy(
								dynamic =
								VirtualStorageDeviceDynamic(
										id = disk.id,
										allocations = listOf(allocation)
								)
						)
					}
			)

}