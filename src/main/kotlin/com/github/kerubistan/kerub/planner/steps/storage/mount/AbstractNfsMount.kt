package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import io.github.kerubistan.kroki.collections.update

abstract class AbstractNfsMount : AbstractOperationalStep {
	abstract val host: Host

	final override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				hostData.copy(
						config = (hostData.config ?: HostConfiguration(id = hostData.stat.id)).let {
							it.copy(
									services = updateHostConfiguration(it)
							)
						}
				)
			}
	)

	abstract fun updateHostConfiguration(config: HostConfiguration): List<HostService>

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))

}