package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor
import com.github.K0zka.kerub.utils.getLogger

public class StartVirtualMachineExecutor(val hostManager: HostManager) : HypervisorStepExcecutor<StartVirtualMachine>(hostManager) {
	override fun execute(hypervisor: Hypervisor, step: StartVirtualMachine) {
		hypervisor.startVm(step.vm)
	}
}