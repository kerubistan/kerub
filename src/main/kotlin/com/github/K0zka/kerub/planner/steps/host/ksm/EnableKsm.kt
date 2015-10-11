package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.ComputationCost
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.utils.sum
import java.math.BigInteger

data class EnableKsm(val host: Host) : AbstractOperationalStep() {

	private fun totalMemoryUsedByVms(state: OperationalState): BigInteger =
			state.vmDyns.values().map { if (it.hostId == host.id) it.memoryUsed else BigInteger.ZERO }.sum()

	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}

	override fun getCost(state: OperationalState): List<Cost> {
		return listOf(ComputationCost(
				host = host,
				cycles = (totalMemoryUsedByVms(state).longValue())
		                             )
		             )
	}

}