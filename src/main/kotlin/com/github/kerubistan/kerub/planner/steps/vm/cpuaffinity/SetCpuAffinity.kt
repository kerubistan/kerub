package com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import com.github.kerubistan.kerub.utils.update

@JsonTypeName("set-cpu-affinity")
data class SetCpuAffinity(val vm: VirtualMachine, val cpus: List<Int>, override val host: Host) : HostStep {

	override fun reservations(): List<Reservation<*>> = listOf(VmReservation(vm), UseHostReservation(host))

	override fun take(state: OperationalState) = state.copy(
			vms = state.vms.update(vm.id) { vmData ->
				val dynamic = requireNotNull(vmData.dynamic)
				vmData.copy(dynamic = dynamic.copy(
						coreAffinity = cpus
				))
			}
	)
}