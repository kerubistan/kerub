package com.github.kerubistan.kerub.planner.steps.vstorage.fs.create

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.utils.update

data class CreateImage(
		override val disk: VirtualStorageDevice,
		override val capability: FsStorageCapability,
		override val host: Host,
		val format: VirtualDiskFormat) : AbstractCreateVirtualStorage<VirtualStorageFsAllocation, FsStorageCapability> {
	override val allocation: VirtualStorageFsAllocation by lazy {
		VirtualStorageFsAllocation(
				hostId = host.id,
				actualSize = disk.size, //TODO not true when thin provisioning
				mountPoint = path,
				type = format,
				fileName = "$path/${disk.id}",
				capabilityId = capability.id
		)
	}

	val path get() = capability.mountPoint

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