package com.github.kerubistan.kerub.planner.steps.vstorage.remove

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

data class RemoveVirtualStorage(val vStorage: VirtualStorageDevice) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage - vStorage.id
	)

	override fun reservations(): List<Reservation<*>> = listOf(VirtualStorageReservation(vStorage))
}