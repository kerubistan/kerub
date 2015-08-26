package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.k0zka.finder4j.backtrack.StepFactory

public object MigrateVirtualMachineFactory : StepFactory<MigrateVirtualMachine, OperationalState>{
	override fun produce(state: OperationalState?): List<MigrateVirtualMachine>? {
		throw UnsupportedOperationException()
	}
}