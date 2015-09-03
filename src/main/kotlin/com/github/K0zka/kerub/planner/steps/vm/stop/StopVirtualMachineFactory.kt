package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.k0zka.finder4j.backtrack.StepFactory

public object StopVirtualMachineFactory : AbstractOperationalStepFactory<StopVirtualMachine>() {
	override fun produce(state: OperationalState): List<StopVirtualMachine> {
		return listOf()
	}
}