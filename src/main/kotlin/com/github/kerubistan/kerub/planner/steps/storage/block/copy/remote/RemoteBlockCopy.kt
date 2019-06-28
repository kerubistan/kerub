package com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.AbstractBlockCopy

data class RemoteBlockCopy(
		override val sourceDevice: VirtualStorageDevice,
		override val targetDevice: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageBlockDeviceAllocation,
		val sourceHost: Host,
		override val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>) : AbstractBlockCopy() {

	init {
		validate()
		check(sourceHost.id != allocationStep.host.id) {
			"source host (${sourceHost.id}) must not be the same as the target host (${allocationStep.host.id})"
		}
	}

	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(allocationStep.host),
			UseHostReservation(sourceHost),
			VirtualStorageReservation(device = sourceDevice),
			VirtualStorageReservation(device = targetDevice)
	)
}