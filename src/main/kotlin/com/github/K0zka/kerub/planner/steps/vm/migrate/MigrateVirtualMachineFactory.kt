package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object MigrateVirtualMachineFactory : AbstractOperationalStepFactory<MigrateVirtualMachine>() {
	override fun produce(state: OperationalState): List<MigrateVirtualMachine> {
		return listOf()
	}
}