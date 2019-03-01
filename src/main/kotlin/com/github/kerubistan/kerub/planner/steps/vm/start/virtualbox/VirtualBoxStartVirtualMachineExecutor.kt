package com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.virt.vbox.VBoxManage

class VirtualBoxStartVirtualMachineExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val vmDynDao: VirtualMachineDynamicDao)
	: AbstractStepExecutor<VirtualBoxStartVirtualMachine, Unit>() {
	override fun update(step: VirtualBoxStartVirtualMachine, updates: Unit) {
		val dyn = vmDynDao[step.vm.id] ?: VirtualMachineDynamic(
				id = step.vm.id,
				status = VirtualMachineStatus.Up,
				hostId = step.host.id,
				memoryUsed = step.vm.memory.max)
		vmDynDao.update(dyn.copy(
				hostId = step.host.id,
				status = VirtualMachineStatus.Up
		))
	}

	override fun perform(step: VirtualBoxStartVirtualMachine) {
		hostCommandExecutor.execute(step.host) {
			session ->
			VBoxManage.startVm(
					session = session,
					vm = step.vm,
					targetHost = step.host,
					storageMap = mapOf() //TODO
			)
		}
	}
}