package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("pause-vm")
data class PauseVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {

	override fun reservations(): List<Reservation<*>> = listOf(VmReservation(vm))

	override fun take(state: OperationalState): OperationalState {
		//TODO: should also transform host CPU load data to show any useful
		return state.copy(
				vms = state.vms.update(vm.id) { vmData ->
					vmData.copy(
							dynamic = requireNotNull(vmData.dynamic).copy(
									status = VirtualMachineStatus.Paused,
									cpuUsage = listOf()
							)
					)
				}
		)
	}
}