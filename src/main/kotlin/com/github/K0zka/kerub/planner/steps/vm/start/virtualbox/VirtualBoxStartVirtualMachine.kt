package com.github.K0zka.kerub.planner.steps.vm.start.virtualbox

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update
import java.math.BigInteger

data class VirtualBoxStartVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {
	override fun reservations(): List<Reservation<*>>
			= listOf(UseHostReservation(host), VmReservation(vm))

	override fun take(state: OperationalState): OperationalState = state.copy(
			vms = state.vms.update(vm.id) {
				it.copy(
						dynamic = VirtualMachineDynamic(
								id = vm.id,
								status = VirtualMachineStatus.Up,
								hostId = host.id,
								memoryUsed = vm.memory.min
						)
				)
			},
			hosts = state.hosts.update(host.id) {
				hostData ->
				val dynamic = requireNotNull(hostData.dynamic)
				hostData.copy(
						dynamic = dynamic.copy(
								memFree = dynamic.memFree ?: host.capabilities?.totalMemory ?: BigInteger.ZERO - vm.memory.min
						)
				)
			}
	)
}