package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.DisplaySettings
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor
import com.github.K0zka.kerub.utils.genPassword

class StartVirtualMachineExecutor(hostManager: HostManager, private val vmDynDao: VirtualMachineDynamicDao) : HypervisorStepExcecutor<StartVirtualMachine>(hostManager) {
	override fun update(step: StartVirtualMachine) {
		val protoAndPort = getHypervisor(step).getDisplay(step.vm)

		vmDynDao.update(step.vm.id, {
			it.copy(
					status = VirtualMachineStatus.Up,
					displaySetting = DisplaySettings(
							hostAddr = step.host.address,
							password = step.consolePassword.value,
							ca = "",
							port = protoAndPort.second
					)
			)
		})
	}

	override fun execute(hypervisor: Hypervisor, step: StartVirtualMachine) {
		val consolePwd = genPassword(length = 128)
		hypervisor.startVm(step.vm, consolePwd)
	}
}