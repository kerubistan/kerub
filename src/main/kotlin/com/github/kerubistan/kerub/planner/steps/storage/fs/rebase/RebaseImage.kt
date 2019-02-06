package com.github.kerubistan.kerub.planner.steps.storage.fs.rebase

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

data class RebaseImage(val virtualStorage: VirtualStorageDevice, val allocation: VirtualStorageAllocation)
	: AbstractOperationalStep {

	override fun take(state: OperationalState): OperationalState {
		TODO()
	}

	override fun reservations(): List<Reservation<*>> {
		TODO()
	}
}