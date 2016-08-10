package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update
import java.math.BigInteger

data class VirtualBoxStartVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {
	override fun take(state: OperationalState): OperationalState = state.copy(
			vmDyns = state.vmDyns + (vm.id to VirtualMachineDynamic(
					id = vm.id,
					status = VirtualMachineStatus.Up,
					hostId = host.id,
					memoryUsed = vm.memory.min
			)),
			hostDyns = state.hostDyns.update(host.id) {
				dyn ->
				dyn.copy(
						memFree = dyn.memFree ?: host.capabilities?.totalMemory ?: BigInteger.ZERO - vm.memory.min
				)
			}
	)
}