package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object VirtualBoxStartVirtualMachineFactory : AbstractOperationalStepFactory<VirtualBoxStartVirtualMachine>() {
	override fun produce(state: OperationalState): List<VirtualBoxStartVirtualMachine> {
		TODO()
	}
}