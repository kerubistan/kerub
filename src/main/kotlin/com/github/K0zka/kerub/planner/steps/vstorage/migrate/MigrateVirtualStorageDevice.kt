package com.github.K0zka.kerub.planner.steps.vstorage.migrate

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.HostStorageReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public class MigrateVirtualStorageDevice(
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