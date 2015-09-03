package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public class StartVirtualMachine(val vm: VirtualMachine, val host: Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		val hostDynMap = state.hostDyns.toMap { it.id }
		val hostDyn = hostDynMap[host.id]

		return state.copy(
				vmDyns = state.vmDyns + VirtualMachineDynamic(
						lastUpdated = System.currentTimeMillis(),
						id = vm.id,
						hostId = host.id,
				        memoryUsed = vm.memoryMb.min.toLong() * 1024 * 1024
				                                             ),
				hostDyns = (state.hostDyns.filter { it.id != host.id }) + hostDyn!!
		          )
	}
}