package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.AbstractBlockCopy

data class LocalBlockCopy(
		override val sourceDevice: VirtualStorageDevice,
		override val targetDevice: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageBlockDeviceAllocation,
		override val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>
) : AbstractBlockCopy() {

	init {
		validate()
		check(sourceAllocation.hostId == allocationStep.host.id) {
			"source allocation host (${sourceAllocation.hostId}) must be the same as the target allocation " +
					"host (${allocationStep.host.id})"
		}
	}

	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host = allocationStep.host),
			VirtualStorageReservation(device = sourceDevice)
	)
}