package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.k0zka.finder4j.backtrack.StepFactory

public object StartVirtualMachineFactory : AbstractOperationalStepFactory<StartVirtualMachine>() {
	override fun produce(state: OperationalState): List<StartVirtualMachine> {
		return listOf()
	}
}