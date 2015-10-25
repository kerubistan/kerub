package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor

public class MigrateVirtualMachineExecutor(val hostManager : HostManager) : StepExecutor<MigrateVirtualMachine> {
	override fun execute(step: MigrateVirtualMachine) {
		val hypervisor = hostManager.getHypervisor(step.target)
		if(hypervisor == null) {
			throw IllegalStateException("No hypervisor on host")
		} else {
			hypervisor.migrate(
					vm = step.vm,
					source = step.source,
					target = step.target)
		}
	}
}