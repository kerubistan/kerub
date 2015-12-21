package com.github.K0zka.kerub.planner.steps.vm.resume

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class ResumeVirtualMachineExecutor(hostManager : HostManager) : HypervisorStepExcecutor<ResumeVirtualMachine>(hostManager) {
	override fun execute(hypervisor: Hypervisor, step: ResumeVirtualMachine) {
		hypervisor.resume(step.vm)
	}
}