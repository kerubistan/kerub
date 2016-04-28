package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class PauseVirtualMachineExecutor(
		hostManager: HostManager,
		private val vmDynDao: VirtualMachineDynamicDao
) : HypervisorStepExcecutor<PauseVirtualMachine, Unit>(hostManager) {
	override fun update(step: PauseVirtualMachine, updates: Unit) {
		vmDynDao.update(step.vm.id, {
			dyn ->
			dyn.copy(
					status = VirtualMachineStatus.Paused
			)
		})
	}

	override fun execute(hypervisor: Hypervisor, step: PauseVirtualMachine) {
		hypervisor.suspend(step.vm)
	}
}