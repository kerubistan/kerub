package com.github.K0zka.kerub.planner.steps.vm.migrate.kvm

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor

class KvmMigrateVirtualMachineExecutor(val hostManager: HostManager) : StepExecutor<KvmMigrateVirtualMachine> {
	override fun execute(step: KvmMigrateVirtualMachine) {
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