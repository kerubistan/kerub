package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class PauseVirtualMachineExecutor(
		hostManager: HostManager,
		private val vmDynDao: VirtualMachineDynamicDao
) : HypervisorStepExcecutor<PauseVirtualMachine, Unit>(hostManager) {
	override fun update(step: PauseVirtualMachine, updates: Unit) {
		vmDynDao.update(step.vm.id) { dyn ->
			dyn.copy(
					status = VirtualMachineStatus.Paused
			)
		}
	}

	override fun execute(hypervisor: Hypervisor, step: PauseVirtualMachine) {
		hypervisor.suspend(step.vm)
	}
}