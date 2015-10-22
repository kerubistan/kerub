package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.ComputationCost
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.utils.sum
import java.math.BigInteger

data class EnableKsm(val host: Host, val cycles : Long) : AbstractOperationalStep() {

	companion object {
		//TODO: this depends on the operational state
		// size and number of virtual machines,
		// ther operating systems, etc
		// so basically this is just a placeholder
		val ksmGeneratedLoad = 5.toByte()
	}

	override fun take(state: OperationalState): OperationalState {
		val dyn = requireNotNull(state.hostDyns[host.id])

		return state.copy(
				hostDyns = state.hostDyns + (host.id to dyn.copy(
						ksmEnabled = true,
				        systemCpu = ((dyn.systemCpu ?: 0) + ksmGeneratedLoad).toByte(),
				        idleCpu = ((dyn.idleCpu ?: 0) + ksmGeneratedLoad).toByte(),
				        memFree = ((dyn.memFree))
				                                                ))
		                 )
	}

	override fun getCost(): List<Cost> {
		return listOf(ComputationCost(
				host = host,
				cycles = cycles
		                             )
		             )
	}

}