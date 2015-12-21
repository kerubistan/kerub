package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.CpuStat
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update

class PauseVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {
	override fun take(state: OperationalState): OperationalState {
		//TODO: should also transform host CPU load data to show any useful
		return state.copy(
				vmDyns = state.vmDyns.update(vm.id, {
					it.copy(
							status = VirtualMachineStatus.Paused,
							cpuUsage = CpuStat.zero
					)
				})
		)
	}
}