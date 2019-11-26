package com.github.kerubistan.kerub.planner.steps.vm.stop

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.Risk
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import io.github.kerubistan.kroki.collections.update
import java.math.BigInteger

/**
 * Stop virtual machine.
 * Operation cost is considered negligible.
 */
@JsonTypeName("stop-vm")
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
									memFree = ((hostDyn.memFree ?: BigInteger.ZERO) - vm.memory.max).coerceAtLeast(
											BigInteger.ZERO)
							)
					)
				}
		)

	}

	override fun getCost(): List<Cost> {
		val availablityExpectation =
				vm.expectations.firstOrNull { it is VirtualMachineAvailabilityExpectation && it.up }
						as VirtualMachineAvailabilityExpectation?
		return if (availablityExpectation == null) {
			listOf()
		} else {
			listOf(Risk(score = score(availablityExpectation.level), comment = ""))
		}
	}

	private fun score(level: ExpectationLevel): Int =
			scores[level] ?: 0

	override fun reservations() = listOf(
			VmReservation(vm),
			UseHostReservation(host)
	)
}