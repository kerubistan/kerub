package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.utils.getLogger

public class StartVirtualMachineExecutor(val hostManager: HostManager) : StepExecutor<StartVirtualMachine> {
	override fun execute(step: StartVirtualMachine) {
		val hypervisor = requireNotNull(
				hostManager.getHypervisor(step.host),
				{ "Can not start vm ${step.vm.id} - there is no hypervisor on the host ${step.host.id}" }
		)
		hypervisor.startVm(step.vm)
	}
}