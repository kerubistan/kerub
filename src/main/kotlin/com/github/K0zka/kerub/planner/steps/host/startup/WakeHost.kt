package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.TimeCost
import com.github.K0zka.kerub.planner.reservations.FullHostReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import java.math.BigInteger

public class WakeHost(val host: Host) : AbstractOperationalStep {
	override fun reservations(): List<Reservation<Host>>
			= listOf(FullHostReservation(host))

	override fun take(state: OperationalState): OperationalState {
		val otherHosts = state.hostDyns.filter { it.value.id != host.id }

		val dyn = state.hostDyns[host.id]
				?: HostDynamic(
				id = host.id,
				status = HostStatus.Up,
				memFree = host.capabilities?.totalMemory,
				memUsed = BigInteger.ZERO,
				userCpu = 0,
				systemCpu = 0
		)

		return state.copy(
				hostDyns = otherHosts + (host.id to dyn)
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