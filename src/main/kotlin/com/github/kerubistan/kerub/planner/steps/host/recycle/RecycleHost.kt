package com.github.kerubistan.kerub.planner.steps.host.recycle

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

@JsonTypeName("host-recycle")
data class RecycleHost(val host: Host) : AbstractOperationalStep {

	override fun take(state: OperationalState): OperationalState =
			state.copy(
					hosts = state.hosts - host.id
			)

	override fun reservations(): List<Reservation<*>> = listOf(FullHostReservation(host))
}