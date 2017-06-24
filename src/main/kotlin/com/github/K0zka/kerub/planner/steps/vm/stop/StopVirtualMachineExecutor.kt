package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor

class StopVirtualMachineExecutor(hostManager: HostManager, private val vmDynDao: VirtualMachineDynamicDao) : HypervisorStepExcecutor<StopVirtualMachine, Unit>(hostManager) {
	override fun execute(hypervisor: Hypervisor, step: StopVirtualMachine) {
		hypervisor.stopVm(step.vm)
	}

	override fun update(step: StopVirtualMachine, updates: Unit) {
		vmDynDao.remove(step.vm.id)
	}

}