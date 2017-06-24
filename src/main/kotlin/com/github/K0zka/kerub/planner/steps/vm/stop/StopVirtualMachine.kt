package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.Risk
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update
import java.math.BigInteger

/**
 * Stop virtual machine.
 * Operation cost is considered negligible.
 */
data class StopVirtualMachine(val vm: VirtualMachine, override val host: Host) : AbstractOperationalStep, HostStep {

	companion object {
		val scores = mapOf(
				ExpectationLevel.Wish to 1,
				ExpectationLevel.Want to 15,
				ExpectationLevel.DealBreaker to 100
		)
	}

	override fun take(state: OperationalState): OperationalState {
		val hostDyn = requireNotNull(state.hosts[host.id]?.dynamic)
		return state.copy(
				vms = state.vms.update(vm.id) {
					it.copy(
							dynamic = null
					)
				},
				hosts = state.hosts.update(host.id) {
					it.copy(
							dynamic = hostDyn.copy(
									memFree = (hostDyn.memFree ?: BigInteger.ZERO) - vm.memory.max
							)
					)
				}
		)

	}

	override fun getCost(): List<Cost> {
		val availablityExpectation = vm.expectations.firstOrNull { it is VirtualMachineAvailabilityExpectation && it.up }
				as VirtualMachineAvailabilityExpectation?
		if (availablityExpectation == null) {
			return listOf()
		} else {
			return listOf(Risk(score = score(availablityExpectation.level), comment = ""))
		}
	}

	private fun score(level: ExpectationLevel): Int =
			scores[level] ?: 0

	override fun reservations() = listOf(
			VmReservation(vm),
			UseHostReservation(host)
	)
}