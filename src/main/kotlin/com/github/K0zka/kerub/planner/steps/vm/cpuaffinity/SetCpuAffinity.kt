package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

class SetCpuAffinity(val vm: VirtualMachine, cpus: List<Int>) : AbstractOperationalStep {

	override fun reservations(): List<Reservation<VirtualMachine>>
			= listOf(VmReservation(vm))

	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}