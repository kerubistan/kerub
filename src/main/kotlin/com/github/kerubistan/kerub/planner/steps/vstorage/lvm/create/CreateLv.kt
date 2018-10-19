package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.base.updateHostDynLvmWithAllocation
import com.github.kerubistan.kerub.utils.update

data class CreateLv(
		override val host: Host,
		override val volumeGroupName: String,
		override val disk: VirtualStorageDevice
) : AbstractCreateLv() {
	override val allocation: VirtualStorageLvmAllocation by lazy {
		VirtualStorageLvmAllocation(
				hostId = host.id,
				actualSize = disk.size,
				path = "/dev/$volumeGroupName/${disk.id}",
				vgName = volumeGroupName
		)
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage.update(disk.id) {
				it.copy(dynamic = VirtualStorageDeviceDynamic(
						id = disk.id,
						allocations = listOf(allocation)
				))
			},
			hosts = state.hosts.update(host.id) {
				it.copy(dynamic = updateHostDynLvmWithAllocation(state, host, volumeGroupName, disk.size))
			}
	)

}