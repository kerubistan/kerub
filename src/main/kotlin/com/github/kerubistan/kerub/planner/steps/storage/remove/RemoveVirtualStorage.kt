package com.github.kerubistan.kerub.planner.steps.storage.remove

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

@JsonTypeName("remove-virtual-storage")
data class RemoveVirtualStorage(val virtualStorage: VirtualStorageDevice) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage - virtualStorage.id
	)

	override fun reservations(): List<Reservation<*>> = listOf(VirtualStorageReservation(virtualStorage))
}