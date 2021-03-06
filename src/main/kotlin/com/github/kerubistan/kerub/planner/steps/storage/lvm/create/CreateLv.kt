package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.lvm.base.updateHostDynLvmWithAllocation
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("create-lv")
data class CreateLv(
		override val host: Host,
		override val capability: LvmStorageCapability,
		override val disk: VirtualStorageDevice
) : AbstractCreateLv() {

	@get:JsonIgnore
	override val allocation: VirtualStorageLvmAllocation by lazy {
		VirtualStorageLvmAllocation(
				hostId = host.id,
				actualSize = disk.size,
				path = "/dev/$volumeGroupName/${disk.id}",
				vgName = volumeGroupName,
				capabilityId = capability.id
		)
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage.update(disk.id) {
				it.copy(
						dynamic = VirtualStorageDeviceDynamic(
								id = disk.id,
								allocations = listOf(allocation)
						))
			},
			hosts = state.hosts.update(host.id) {
				it.copy(dynamic = updateHostDynLvmWithAllocation(state, host, volumeGroupName, disk.size))
			}
	)

}