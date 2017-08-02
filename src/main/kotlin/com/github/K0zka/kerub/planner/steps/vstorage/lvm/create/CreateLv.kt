package com.github.K0zka.kerub.planner.steps.vstorage.lvm.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.IOCost
import com.github.K0zka.kerub.planner.steps.replace
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import com.github.K0zka.kerub.utils.update

class CreateLv(
		override val host: Host,
		val volumeGroupName: String,
		override val disk: VirtualStorageDevice
) : AbstractCreateVirtualStorage {

	override fun getCost(): List<Cost> {
		return listOf(
				IOCost(2048, host)
		)
	}

	override fun take(state: OperationalState): OperationalState {
		val vStorageDyn = VirtualStorageDeviceDynamic(
				id = disk.id,
				allocations = listOf(VirtualStorageLvmAllocation(
						hostId = host.id,
						actualSize = disk.size,
						path = ""
				))
		)
		val originalHostDyn = requireNotNull(state.hosts[host.id]?.dynamic)
		val volGroup = requireNotNull(host.capabilities?.storageCapabilities?.first { it is LvmStorageCapability && it.volumeGroupName == volumeGroupName })
		val hostDyn = originalHostDyn.copy(
				storageStatus = originalHostDyn.storageStatus.replace({ it.id == volGroup.id }, {
					it.copy(
							freeCapacity = (it.freeCapacity - disk.size)
					)
				})
		)
		return state.copy(
				vStorage = state.vStorage.update(disk.id) {
					it.copy(dynamic = vStorageDyn)
				},
				hosts = state.hosts.update(host.id) {
					it.copy(dynamic = hostDyn)
				}
		)
	}
}