package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.k0zka.finder4j.backtrack.StepFactory

public object StartVirtualMachineFactory : AbstractOperationalStepFactory<StartVirtualMachine>() {
	override fun produce(state: OperationalState): List<StartVirtualMachine> {
		val vmsToRun = state.vms.filter {
			it.expectations.any {
				it is VirtualMachineAvailabilityExpectation
				&& it.up
			}
		}
		val vmsActuallyRunning = state.vmDyns.map { it.id }
		val vmsToStart = vmsToRun.filter { !vmsActuallyRunning.contains(it.id) }



		return listOf()
	}
}