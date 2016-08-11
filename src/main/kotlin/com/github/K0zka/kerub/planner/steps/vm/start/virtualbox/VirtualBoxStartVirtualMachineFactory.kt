package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.match
import com.github.K0zka.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.K0zka.kerub.planner.steps.vm.storageAllocationMap
import com.github.K0zka.kerub.utils.equalsAnyOf
import com.github.K0zka.kerub.utils.times

object VirtualBoxStartVirtualMachineFactory : AbstractStartVmFactory<VirtualBoxStartVirtualMachine>() {
	override fun produce(state: OperationalState): List<VirtualBoxStartVirtualMachine> =
			(getVmsToStart(state) * getWorkingHosts(state) { host, dyn -> isVirtualBoxInstalled(host) }.toList()).filter {
				val vm = it.first
				val host = it.second.first
				match(
						host = host,
						vm = vm,
						dyn = it.second.second,
						vStorage = storageAllocationMap(state)
				)
			}.map { VirtualBoxStartVirtualMachine(vm = it.first, host = it.second.first) }

	fun isVirtualBoxInstalled(host: Host)
			= host.capabilities?.installedSoftware?.any { it.name.equalsAnyOf("VirtualBox", "virtualbox-ose") } ?: false
}