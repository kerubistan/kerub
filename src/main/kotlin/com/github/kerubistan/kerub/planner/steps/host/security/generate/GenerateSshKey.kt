package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKey
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKey
import com.github.kerubistan.kerub.utils.update

data class GenerateSshKey(val host: Host) : AbstractOperationalStep, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep): Boolean = other is ClearSshKey && other.host == host

	override val useBefore get() = listOf(InstallPublicKey::class)

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) {
				it.copy(
						config = (it.config ?: HostConfiguration(id = host.id))
								.copy(publicKey = "PLAN-KEY-FOR-${host.id}")
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}