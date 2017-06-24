package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.junix.virt.vbox.VBoxManage

class VirtualBoxStartVirtualMachineExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val vmDynDao: VirtualMachineDynamicDao)
	: AbstractStepExecutor<VirtualBoxStartVirtualMachine, Unit>() {
	override fun update(step: VirtualBoxStartVirtualMachine, updates: Unit) {
		val dyn = vmDynDao.get(step.vm.id) ?: VirtualMachineDynamic(
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