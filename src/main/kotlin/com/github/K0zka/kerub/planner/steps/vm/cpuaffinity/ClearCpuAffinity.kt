package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update

class ClearCpuAffinity(val vm: VirtualMachine, override val host: Host) : HostStep {
	override fun take(state: OperationalState): OperationalState = state.copy(
			vms = state.vms.update(vm.id) {
				vmData ->
				vmData.copy(
						dynamic = requireNotNull(vmData.dynamic).copy(
								cpuAffinity = null
						)
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(
			VmReservation(vm), UseHostReservation(host)
	)
}