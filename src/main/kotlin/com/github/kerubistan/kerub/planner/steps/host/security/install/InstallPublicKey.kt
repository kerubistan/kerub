package com.github.kerubistan.kerub.planner.steps.host.security.install

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKey
import com.github.kerubistan.kerub.utils.update

data class InstallPublicKey(val sourceHost: Host, val targetHost: Host, val publicKey: String) :
		AbstractOperationalStep, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep): Boolean = other is RemovePublicKey
			&& other.host == targetHost
			&& other.publicKey == publicKey

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(targetHost.id) { coll ->
				coll.copy(
						config = (coll.config ?: HostConfiguration(id = targetHost.id))
								.let { it.copy(acceptedPublicKeys = it.acceptedPublicKeys + publicKey) }
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(targetHost))
}