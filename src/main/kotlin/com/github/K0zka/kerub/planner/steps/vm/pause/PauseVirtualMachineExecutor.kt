package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class PauseVirtualMachineExecutor(hostManager: HostManager) : HypervisorStepExcecutor<PauseVirtualMachine>(hostManager) {
	override fun execute(hypervisor: Hypervisor, step: PauseVirtualMachine) {
		hypervisor.suspend(step.vm)
	}
}