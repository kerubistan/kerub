package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.Risk
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import java.math.BigInteger
import kotlin.math.minus

/**
 * Stop virtual machine.
 * Operation cost is considered negligible.
 */
public data class StopVirtualMachine(val vm: VirtualMachine, val host: Host) : AbstractOperationalStep {

	companion object {
		val scores = mapOf<ExpectationLevel, Int>(
				ExpectationLevel.Wish to 1,
				ExpectationLevel.Want to 15,
				ExpectationLevel.DealBreaker to 100
		                                         )
	}

	override fun take(state: OperationalState): OperationalState {
		val hostDyn = state.hostDyns[host.id]!!
		return state.copy(
				vmDyns = state.vmDyns - (vm.id),
				hostDyns = state.hostDyns + (host.id to
						hostDyn.copy(
								memFree = (hostDyn.memFree ?: BigInteger.ZERO) - vm.memory.max
						            ))
		                 )

	}

	override fun getCost(): List<Cost> {
		val availablityExpectation = vm.expectations.firstOrNull { it is VirtualMachineAvailabilityExpectation && it.up }
				as VirtualMachineAvailabilityExpectation?
		if(availablityExpectation == null ) {
			return listOf()
		} else {
			return listOf(Risk(score = score(availablityExpectation.level), comment = ""))
		}
	}

	private fun score(level: ExpectationLevel): Int =
		scores[level] ?: 0
}