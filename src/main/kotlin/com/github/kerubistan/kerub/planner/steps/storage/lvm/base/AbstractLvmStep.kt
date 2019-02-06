package com.github.kerubistan.kerub.planner.steps.storage.lvm.base

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

interface AbstractLvmStep : AbstractOperationalStep {
	val host: Host
	val volumeGroupName: String
	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}