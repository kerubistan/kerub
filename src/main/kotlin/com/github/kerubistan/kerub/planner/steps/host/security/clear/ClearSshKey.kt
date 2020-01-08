package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKey
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("clear-ssh-key")
data class ClearSshKey(val host: Host) : AbstractOperationalStep, InvertibleStep {
	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) {
				it.copy(
						config = it.config?.copy(publicKey = null)
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))

	override fun isInverseOf(other: AbstractOperationalStep): Boolean = other is GenerateSshKey && other.host == host
}