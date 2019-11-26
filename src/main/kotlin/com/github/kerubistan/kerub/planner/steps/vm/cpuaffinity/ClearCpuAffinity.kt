package com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("clear-cpu-affinity")
data class ClearCpuAffinity(val vm: VirtualMachine, override val host: Host) : HostStep {
	override fun take(state: OperationalState): OperationalState = state.copy(
			vms = state.vms.update(vm.id) {
				vmData ->
				vmData.copy(
						dynamic = requireNotNull(vmData.dynamic).copy(
								coreAffinity = null
						)
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(
			VmReservation(vm), UseHostReservation(host)
	)
}