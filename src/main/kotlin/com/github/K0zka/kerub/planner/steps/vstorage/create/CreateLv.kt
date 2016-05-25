package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.IOCost
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.replace

class CreateLv(
		override val host: Host,
		val volumeGroupName: String,
		override val disk: VirtualStorageDevice
) : AbstractCreate {

	override fun getCost(): List<Cost> {
		return listOf(
				IOCost(2048, host)
		)
	}

	override fun take(state: OperationalState): OperationalState {
		val vStorageDyn = VirtualStorageDeviceDynamic(
				id = disk.id,
				allocation = VirtualStorageLvmAllocation(
						hostId = host.id,
						path = ""
				),
				actualSize = disk.size
		)
		val originalHostDyn = requireNotNull(state.hostDyns[host.id])
		val volGroup = requireNotNull(host.capabilities?.storageCapabilities?.first { it is LvmStorageCapability && it.volumeGroupName == volumeGroupName })
		val hostDyn = originalHostDyn.copy(
				storageStatus = originalHostDyn.storageStatus.replace({ it.id == volGroup.id }, {
					it.copy(
							freeCapacity = (it.freeCapacity - disk.size)
					)
				})
		)
		return state.copy(
				vStorageDyns = state.vStorageDyns + (disk.id to vStorageDyn),
				hostDyns = state.hostDyns + (host.id to hostDyn)
		)
	}
}