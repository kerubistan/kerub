package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.FullHostReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public data class DisableKsm(val host: Host) : AbstractOperationalStep {
	override fun reservations(): List<Reservation>
			= listOf(FullHostReservation(host))

	override fun take(state: OperationalState): OperationalState = state
}