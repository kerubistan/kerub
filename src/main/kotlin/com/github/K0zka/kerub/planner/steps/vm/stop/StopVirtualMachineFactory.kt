package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object StopVirtualMachineFactory : AbstractOperationalStepFactory<StopVirtualMachine>() {
	override fun produce(state: OperationalState): List<StopVirtualMachine> {
		return state.vms.values.filter { it.dynamic?.status == VirtualMachineStatus.Up }
				.map { StopVirtualMachine(it.stat, requireNotNull(state.hosts[it.dynamic?.hostId]?.stat)) }
	}
}