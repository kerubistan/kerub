package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.replace

/**
 * Stop virtual machine.
 * Cost is considered negligible.
 */
public class StopVirtualMachine(val vm: VirtualMachine, val host: Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState =
			state.copy(
					vmDyns = state.vmDyns.replace({ it.id == vm.id }, { it.copy(status = VirtualMachineStatus.Up) }),
					hostDyns = state.hostDyns.replace({ it.id == host.id }, {
						//TODO:
						it.copy(
								memFreeMb = it.memFreeMb?:0 - vm.memoryMb.max
						       )
					})
			          )
}