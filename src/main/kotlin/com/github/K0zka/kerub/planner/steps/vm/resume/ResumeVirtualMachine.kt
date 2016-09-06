package com.github.K0zka.kerub.planner.steps.vm.resume

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update

class ResumeVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {

	override fun reservations(): List<Reservation<*>>
			= listOf(VmReservation(vm))

	override fun take(state: OperationalState): OperationalState {
		return state.copy(
				vms = state.vms.update(vm.id) {
					vmData ->
					vmData.copy(
							dynamic = requireNotNull(vmData.dynamic).copy(status = VirtualMachineStatus.Up)
					)
				}
		)
	}
}