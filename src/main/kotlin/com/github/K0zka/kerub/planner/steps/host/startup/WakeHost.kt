package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.TimeCost
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public class WakeHost(val host: Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		val otherHosts = state.hostDyns.filter { it.getValue().id != host.id }

		val dyn = state.hostDyns[host.id]
				?: HostDynamic(
				id = host.id,
				status = HostStatus.Up
				              )

		return state.copy(
				hostDyns = otherHosts + (host.id to dyn)
		                 )
	}

	/**
	 * TODO
	 * Cost calculation here ignores facts and builds on hardcoded defaults
	 * What should be here in the future:
	 * - decision based on the power management-method
	 * (e.g. WoL could be counted as a risk)
	 * - Estimation based on previous such operations
	 */
	override fun getCost(state: OperationalState): List<Cost> {
		return listOf(
				TimeCost(
						minMs = 10000,
						maxMs = 1000000
				        )
		             )
	}
}