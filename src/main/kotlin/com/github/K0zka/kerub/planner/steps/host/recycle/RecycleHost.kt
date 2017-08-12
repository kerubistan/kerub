package com.github.K0zka.kerub.planner.steps.host.recycle

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.FullHostReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

data class RecycleHost(val host: Host) : AbstractOperationalStep {

	override fun take(state: OperationalState): OperationalState =
			state.copy(
					hosts = state.hosts - host.id
			)

	override fun reservations(): List<Reservation<*>> = listOf(FullHostReservation(host))
}