package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.DisplaySettings
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.steps.vm.base.HypervisorStepExcecutor
import com.github.K0zka.kerub.utils.genPassword
import java.math.BigInteger

class StartVirtualMachineExecutor(hostManager: HostManager, private val vmDynDao: VirtualMachineDynamicDao) : HypervisorStepExcecutor<StartVirtualMachine, DisplaySettings>(hostManager) {

	override fun update(step: StartVirtualMachine, updates: DisplaySettings) {
		val dyn = VirtualMachineDynamic(
				id = step.vm.id,
				displaySetting = updates,
				hostId = step.host.id,
				status = VirtualMachineStatus.Up,
				memoryUsed = BigInteger.ZERO
		)

		vmDynDao.update(dyn)
	}

	override fun execute(hypervisor: Hypervisor, step: StartVirtualMachine) : DisplaySettings {
		val consolePwd = genPassword(length = 16)
		hypervisor.startVm(step.vm, consolePwd)
		val protoAndPort = getHypervisor(step).getDisplay(step.vm)
		val fw = hostManager.getFireWall(step.host).open(protoAndPort.second, "tcp")

		return DisplaySettings(
				hostAddr = step.host.address,
				password = consolePwd,
				ca = "",
				port = protoAndPort.second
		)
	}
}