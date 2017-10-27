package com.github.kerubistan.kerub.planner.steps.host.fence

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

class FenceHost(val host: Host) : AbstractOperationalStep {
	override fun reservations(): List<Reservation<*>>
			= listOf(FullHostReservation(host = host))

	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}