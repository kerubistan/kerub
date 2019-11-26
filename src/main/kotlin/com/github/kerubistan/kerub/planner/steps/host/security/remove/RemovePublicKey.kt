package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKey
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("remove-public-key")
data class RemovePublicKey(val hostOfKey: Host, val host: Host, val publicKey: String) : AbstractOperationalStep,
		InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep): Boolean =
			other is InstallPublicKey && other.targetHost == host && other.publicKey == publicKey

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { coll ->
				coll.copy(
						config = coll.config?.let { it.copy(acceptedPublicKeys = it.acceptedPublicKeys - publicKey) }
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}