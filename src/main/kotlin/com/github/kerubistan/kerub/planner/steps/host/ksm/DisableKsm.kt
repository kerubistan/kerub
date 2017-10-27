package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep

data class DisableKsm(override val host: Host) : HostStep {
	override fun reservations(): List<Reservation<*>>
			= listOf(UseHostReservation(host))

	override fun take(state: OperationalState): OperationalState = state
}