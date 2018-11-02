package com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert.inplace

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert.AbstractConvertImage

data class InPlaceConvertImage(
		override val virtualStorage: VirtualStorageDevice,
		override val fromAllocation: VirtualStorageAllocation)
	: AbstractConvertImage() {
	override fun take(state: OperationalState): OperationalState {
		TODO()
	}

	override fun reservations(): List<Reservation<*>> {
		TODO()
	}
}