package com.github.kerubistan.kerub.planner.steps.vm.stop

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object StopVirtualMachineFactory : AbstractOperationalStepFactory<StopVirtualMachine>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>(VirtualMachineAvailabilityExpectation::class)

	override fun produce(state: OperationalState): List<StopVirtualMachine> {
		return state.vms.values.filter { it.dynamic?.status == VirtualMachineStatus.Up }
				.map { StopVirtualMachine(it.stat, requireNotNull(state.hosts[it.dynamic?.hostId]?.stat)) }
	}
}