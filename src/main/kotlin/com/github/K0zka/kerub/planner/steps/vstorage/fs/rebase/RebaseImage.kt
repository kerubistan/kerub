package com.github.K0zka.kerub.planner.steps.vstorage.fs.rebase

import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

data class RebaseImage(val virtualStorage: VirtualStorageDevice, val allocation: VirtualStorageAllocation)
	: AbstractOperationalStep {

	override fun take(state: OperationalState): OperationalState {
		TODO()
	}

	override fun reservations(): List<Reservation<*>> {
		TODO()
	}
}