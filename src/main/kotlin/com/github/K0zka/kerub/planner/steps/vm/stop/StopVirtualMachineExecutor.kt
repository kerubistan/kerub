package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachineExecutor
import com.github.K0zka.kerub.utils.getLogger

public class StopVirtualMachineExecutor(val hostManager : HostManager) : StepExecutor<StopVirtualMachine>{

	companion object {
		val logger = getLogger(StopVirtualMachineExecutor::class)
	}

	override fun execute(step: StopVirtualMachine) {
		val hypervisor = hostManager.getHypervisor(step.host)
		if(hypervisor == null) {
			logger.error("Can not execute step {} - no ypervisor on host", step)
		} else {
			hypervisor.stopVm(step.vm)
		}
	}
}