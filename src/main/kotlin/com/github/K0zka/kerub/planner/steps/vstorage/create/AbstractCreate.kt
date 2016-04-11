package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

interface  AbstractCreate : AbstractOperationalStep {
	val host : Host
	val disk : VirtualStorageDevice
	override fun reservations(): List<Reservation<*>>
			= listOf(
			VirtualStorageReservation(disk),
			UseHostReservation(host)
	)

}