package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

/**
 * Power down the host
 */
public data class PowerDownHost(val host : Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		return state.copy(
				hostDyns = state.hostDyns - host.id
		                 )
	}
}