package com.github.kerubistan.kerub.planner.steps.vstorage.migrate

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.HostStorageReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

class MigrateVirtualStorageDevice(
		val device: VirtualStorageDevice,
		val source: Host,
		val target: Host
) : AbstractOperationalStep {

	override fun reservations(): List<Reservation<*>>
			= listOf(
			VirtualStorageReservation(device),
			HostStorageReservation(target, device.size),
			UseHostReservation(source)
	)

	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}