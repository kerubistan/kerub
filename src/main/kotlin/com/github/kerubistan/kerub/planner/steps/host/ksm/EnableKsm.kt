package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.ComputationCost
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import com.github.kerubistan.kerub.utils.update

data class EnableKsm(override val host: Host, val cycles: Long) : HostStep, InvertibleStep {

	override fun isInverseOf(other: AbstractOperationalStep) = other is DisableKsm && other.host == this.host

	companion object {
		//TODO: issue #123 this depends on the operational state
		// size and number of virtual machines,
		// ther operating systems, etc
		// so basically this is just a placeholder
		val ksmGeneratedLoad = 5.toByte()
	}

	override fun take(state: OperationalState): OperationalState {
		val dyn = requireNotNull(state.hosts[host.id]?.dynamic)

		return state.copy(
				hosts = state.hosts.update(host.id) { hostData ->
					hostData.copy(
							dynamic = dyn.copy(
									ksmEnabled = true,
									systemCpu = ((dyn.systemCpu ?: 0) + ksmGeneratedLoad).toByte(),
									idleCpu = ((dyn.idleCpu ?: 0) + ksmGeneratedLoad).toByte(),
									memFree = ((dyn.memFree))
							)
					)
				}
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
