package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.utils.getLogger

public class StopVirtualMachineExecutor(val hostManager: HostManager) : StepExecutor<StopVirtualMachine> {
	override fun execute(step: StopVirtualMachine) {
		val hypervisor = requireNotNull(
				hostManager.getHypervisor(step.host),
				{ "Can not stop ${step.vm.id} - no hypervisor on host ${step.host.id}" })
		hypervisor.stopVm(step.vm)
	}
}