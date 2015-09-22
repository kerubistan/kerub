package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.utils.getLogger

public class StartVirtualMachineExecutor(val hostManager : HostManager) : StepExecutor<StartVirtualMachine> {

	companion object {
		val logger = getLogger(StartVirtualMachineExecutor::class)
	}

	override fun execute(step: StartVirtualMachine) {
		val hypervisor = hostManager.getHypervisor(step.host)
		if(hypervisor == null) {
			logger.error("Can not execute step {} - there is no hypervisor on the host", step)
		} else {
			hypervisor.startVm(step.vm)
		}
	}
}