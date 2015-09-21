package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.replace

public class StartVirtualMachine(val vm: VirtualMachine, val host: Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		return state.copy(
				vmDyns = state.vmDyns + VirtualMachineDynamic(
						lastUpdated = System.currentTimeMillis(),
						id = vm.id,
						hostId = host.id,
				        memoryUsed = vm.memoryMb.min.toLong() * 1024 * 1024
				                                             ),
				hostDyns = state.hostDyns.replace({ it.id != host.id }, {
					it.copy(
							idleCpu = it.idleCpu, // TODO - estimate on the virtual machine CPU usage
					        memFreeMb = it.memFreeMb, //TODO - estimate on memory usage of the VM
					        memUsedMb = it.memUsedMb // TODO + estimate on memory usage of the VM
					       )
				}
				                                 )
		          )
	}
}