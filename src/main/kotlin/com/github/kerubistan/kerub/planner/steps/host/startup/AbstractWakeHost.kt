package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update
import java.math.BigInteger

abstract class AbstractWakeHost : AbstractOperationalStep {
	abstract val host: Host

	override fun reservations(): List<Reservation<Host>>
			= listOf(FullHostReservation(host))

	override fun take(state: OperationalState): OperationalState {
		val dyn = HostDynamic(
				id = host.id,
				status = HostStatus.Up,
				memFree = host.capabilities?.totalMemory,
				memUsed = BigInteger.ZERO,
				userCpu = 0,
				systemCpu = 0
		)

		return state.copy(
				hosts = state.hosts.update(host.id) {
					it.copy(
							dynamic = dyn
					)
				}
		)
	}

	/*
	 * TODO - issue #126
	 * Cost calculation here ignores facts and builds on hardcoded defaults
	 * What should be here in the future:
	 * - decision based on the power management-method
	 * (e.g. WoL could be counted as a risk)
	 * - Estimation based on previous such operations
	 */
	override fun getCost(): List<Cost> {
		return listOf(
				TimeCost(
						minMs = 10000,
						maxMs = 1000000
				)
		)
	}
}