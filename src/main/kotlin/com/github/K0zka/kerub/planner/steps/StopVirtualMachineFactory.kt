package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.k0zka.finder4j.backtrack.StepFactory

public object StopVirtualMachineFactory : AbstractOperationalStepFactory<StopVirtualMachine>() {
	override fun produce(state: OperationalState): List<StopVirtualMachine> {
		return listOf()
	}
}