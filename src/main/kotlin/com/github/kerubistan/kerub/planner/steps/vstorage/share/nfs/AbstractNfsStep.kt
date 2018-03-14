package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update

abstract class AbstractNfsStep : AbstractOperationalStep {
	abstract val host : Host
	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host)
	)

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				hostData.copy(
						config = (hostData.config ?: HostConfiguration(id = host.id)).let(::updateHostConfig)
				)
			}
	)

	abstract fun updateHostConfig(config: HostConfiguration) : HostConfiguration
}