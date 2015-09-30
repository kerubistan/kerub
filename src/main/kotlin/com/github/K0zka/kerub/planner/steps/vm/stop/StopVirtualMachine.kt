package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

/**
 * Stop virtual machine.
 * Operation cost is considered negligible.
 */
public class StopVirtualMachine(val vm: VirtualMachine, val host: Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		val hostDyn = state.hostDyns[host.id]!!
		return state.copy(
				vmDyns = state.vmDyns - (vm.id),
				hostDyns = state.hostDyns + (host.id to
						hostDyn.copy(
								memFreeMb = hostDyn.memFreeMb ?: 0 - vm.memoryMb.max
						            ))
		                 )

	}
}