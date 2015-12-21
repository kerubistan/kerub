package com.github.K0zka.kerub.planner.steps.vm.resume

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.base.AbstractForEachVmStepFactory

object ResumeVirtualMachineFactory : AbstractForEachVmStepFactory<ResumeVirtualMachine>() {
	override fun filter(vmDyn: VirtualMachineDynamic): Boolean
			= vmDyn.status == VirtualMachineStatus.Paused

	override fun create(vmDyn: VirtualMachineDynamic, state: OperationalState): ResumeVirtualMachine
			= ResumeVirtualMachine(
			vm = getVm(vmDyn.id, state),
			host = getHost(vmDyn.hostId, state)
	)
}