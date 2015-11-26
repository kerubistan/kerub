package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor

public class MigrateVirtualMachineExecutor(val hostManager: HostManager) : StepExecutor<MigrateVirtualMachine> {
	override fun execute(step: MigrateVirtualMachine) {
		val hypervisor = requireNotNull(
				hostManager.getHypervisor(step.target),
				{ "No hypervisor on host ${step.target.address}" }
		)
		hypervisor.migrate(
				vm = step.vm,
				source = step.source,
				target = step.target)
	}
}