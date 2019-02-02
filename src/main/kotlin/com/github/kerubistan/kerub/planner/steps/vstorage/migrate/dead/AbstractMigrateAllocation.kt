package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.dead

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.base.UnAllocate
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage

abstract class AbstractMigrateAllocation : AbstractOperationalStep {
	abstract val sourceHost: Host
	abstract val targetHost: Host
	abstract val virtualStorage: VirtualStorageDevice
	abstract val sourceAllocation: VirtualStorageAllocation
	abstract val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>
	abstract val deAllocationStep : UnAllocate<*>

	override fun take(state: OperationalState): OperationalState =
			deAllocationStep.take(allocationStep.take(state.copy()))

	protected open fun validate() {
		check(allocationStep.host.id == targetHost.id) {
			"target allocation step is on ${allocationStep.host.id} ${allocationStep.host.address} " +
					" - it must be on the target server ${targetHost.id} ${targetHost.address}"
		}
		check(deAllocationStep.host.id == sourceHost.id) {
			"source allocation step is on ${deAllocationStep.host.id} ${deAllocationStep.host.address} " +
					" - it must be on the target server ${sourceHost.id} ${sourceHost.address}"
		}
		check(sourceHost.id != targetHost.id) {
			"source host must not be the same as target host (${sourceHost.id})"
		}
	}

	override fun reservations() : List<Reservation<*>> = listOf(
			UseHostReservation(sourceHost),
			UseHostReservation(targetHost)
	)

}