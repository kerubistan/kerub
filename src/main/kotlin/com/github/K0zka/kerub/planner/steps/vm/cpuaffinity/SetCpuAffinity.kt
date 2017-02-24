package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update

class SetCpuAffinity(val vm: VirtualMachine, val cpus: List<Int>, override val host: Host) : HostStep {

	override fun reservations(): List<Reservation<*>>
			= listOf(VmReservation(vm), UseHostReservation(host))

	override fun take(state: OperationalState)
			= state.copy(
			vms = state.vms.update(vm.id, {
				vmData ->
				val dynamic = requireNotNull(vmData.dynamic)
				vmData.copy(dynamic = dynamic.copy(
						coreAffinity = cpus
				))
			})
	)
}