package com.github.kerubistan.kerub.planner.steps.vstorage

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

interface AbstractCreateVirtualStorage<A : VirtualStorageAllocation> : AbstractOperationalStep {
	val host: Host
	val disk: VirtualStorageDevice
	val allocation : A
	override fun reservations(): List<Reservation<*>>
			= listOf(
			VirtualStorageReservation(disk),
			UseHostReservation(host)
	)

}