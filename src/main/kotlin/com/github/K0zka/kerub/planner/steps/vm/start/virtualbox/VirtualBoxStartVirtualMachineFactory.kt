package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.start.AbstractStartVmFactory

object VirtualBoxStartVirtualMachineFactory : AbstractStartVmFactory<VirtualBoxStartVirtualMachine>() {
	override fun produce(state: OperationalState): List<VirtualBoxStartVirtualMachine> {
		TODO()
	}
}