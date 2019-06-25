package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.AbstractBlockCopy
import com.github.kerubistan.kerub.utils.update

data class LocalBlockCopy(
		override val sourceDevice: VirtualStorageDevice,
		override val targetDevice: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageBlockDeviceAllocation,
		override val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>
) : AbstractBlockCopy() {

	init {
		check(sourceDevice.id != targetDevice.id) {
			"source device (${sourceDevice.id}) must not be the same as the target device (${targetDevice.id})"
		}
		check(sourceAllocation.hostId == allocationStep.host.id) {
			"source allocation host (${sourceAllocation.hostId}) must be the same as the target allocation " +
					"host (${allocationStep.host.id})"
		}
	}

	override fun take(state: OperationalState): OperationalState = allocationStep.take(state).copy(
			vStorage = state.vStorage.update(targetDevice.id) { virtualStorage ->
				virtualStorage.copy(
						stat = virtualStorage.stat.copy(
								expectations = virtualStorage.stat.expectations
										.filterNot { it is CloneOfStorageExpectation }
						),
						dynamic = virtualStorage.updateDynamic { dyn ->
							dyn.copy(allocations = listOf(allocationStep.allocation))
						}
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host = allocationStep.host),
			VirtualStorageReservation(device = sourceDevice)
	)
}