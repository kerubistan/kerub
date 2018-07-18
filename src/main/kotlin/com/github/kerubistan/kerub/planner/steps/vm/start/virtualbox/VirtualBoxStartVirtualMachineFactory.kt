package com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vm.match
import com.github.kerubistan.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.kerubistan.kerub.utils.junix.virt.vbox.VBoxManage
import com.github.kerubistan.kerub.utils.times

object VirtualBoxStartVirtualMachineFactory : AbstractStartVmFactory<VirtualBoxStartVirtualMachine>() {
	override fun produce(state: OperationalState): List<VirtualBoxStartVirtualMachine> =
			(getVmsToStart(state) * getWorkingHosts(state) { hostData ->
				VBoxManage.available(hostData.stat.capabilities)
						&& isHwVirtualizationSupported(hostData.stat)
			}.toList()).filter {
				val vm = it.first
				val host = it.second
				match(
						host = host,
						vm = vm
				)
			}.map { VirtualBoxStartVirtualMachine(vm = it.first, host = it.second.stat) }

}