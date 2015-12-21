package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.base.AbstractForEachVmStepFactory

object PauseVirtualMachineFactory : AbstractForEachVmStepFactory<PauseVirtualMachine>() {

	override fun filter(vmDyn: VirtualMachineDynamic): Boolean
			= vmDyn.status == VirtualMachineStatus.Up

	override fun create(vmDyn: VirtualMachineDynamic, state: OperationalState): PauseVirtualMachine =
			PauseVirtualMachine(
					vm = getVm(vmDyn.id, state),
					host = getHost(vmDyn.hostId, state)
			)
}