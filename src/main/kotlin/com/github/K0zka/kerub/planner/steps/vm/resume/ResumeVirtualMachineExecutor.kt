package com.github.K0zka.kerub.planner.steps.vm.resume

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class ResumeVirtualMachineExecutor(hostManager : HostManager, private val vmDynDao : VirtualMachineDynamicDao) : HypervisorStepExcecutor<ResumeVirtualMachine>(hostManager) {
	override fun update(step: ResumeVirtualMachine) {
		vmDynDao.update(step.vm.id, {
			it.copy(
					status = VirtualMachineStatus.Up
			)
		})
	}

	override fun execute(hypervisor: Hypervisor, step: ResumeVirtualMachine) {
		hypervisor.resume(step.vm)
	}
}