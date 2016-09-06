package com.github.K0zka.kerub.planner.steps.vstorage.fs.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import com.github.K0zka.kerub.utils.update

data class CreateImage(
		override val disk: VirtualStorageDevice,
		override val host: Host,
		val path: String,
		val format: VirtualDiskFormat) : AbstractCreateVirtualStorage {

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
										allocation = VirtualStorageFsAllocation(
												hostId = host.id,
												mountPoint = "",
												type = VirtualDiskFormat.qcow2
										),
										actualSize = disk.size //TODO not true when thin provisioning
								)
						)
					}
			)

}