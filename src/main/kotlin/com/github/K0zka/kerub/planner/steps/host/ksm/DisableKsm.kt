package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep

data class DisableKsm(override val host: Host) : HostStep {
	override fun reservations(): List<Reservation<*>>
			= listOf(UseHostReservation(host))

	override fun take(state: OperationalState): OperationalState = state
}