package com.github.kerubistan.kerub.planner.steps.vm.resume

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import com.github.kerubistan.kerub.utils.update

data class ResumeVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {

	override fun reservations(): List<Reservation<*>> = listOf(VmReservation(vm))

	override fun take(state: OperationalState): OperationalState {
		return state.copy(
				vms = state.vms.update(vm.id) { vmData ->
					vmData.copy(
							dynamic = requireNotNull(vmData.dynamic).copy(status = VirtualMachineStatus.Up)
					)
				}
		)
	}
}