package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("host-power-down")
data class PowerDownHost(val host: Host) : AbstractOperationalStep {
	override fun getCost(): List<Cost> {
		return listOf()
	}

	override fun reservations(): List<Reservation<Host>>
			= listOf(FullHostReservation(host))

	override fun take(state: OperationalState): OperationalState {
		return state.copy(
				hosts = state.hosts.update(host.id) {
					it.copy(
							dynamic = null
					)
				}
		)
	}
}