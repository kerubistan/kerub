package com.github.kerubistan.kerub.planner.steps.vm.resume

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class ResumeVirtualMachineExecutor(hostManager: HostManager, private val vmDynDao: VirtualMachineDynamicDao) : HypervisorStepExcecutor<ResumeVirtualMachine, Unit>(hostManager) {
	override fun update(step: ResumeVirtualMachine, updates: Unit) {
		vmDynDao.update(step.vm.id) {
			it.copy(
					status = VirtualMachineStatus.Up
			)
		}
	}

	override fun execute(hypervisor: Hypervisor, step: ResumeVirtualMachine) {
		hypervisor.resume(step.vm)
	}
}