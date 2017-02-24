package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.HostMemoryReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update

data class KvmStartVirtualMachine(val vm: VirtualMachine, override val host: Host) : HostStep {
	override fun take(state: OperationalState): OperationalState {
		val hostDyn = requireNotNull(state.hosts[host.id]?.dynamic)
		return state.copy(
				vms = state.vms.update(vm.id) {
					vmData ->
					vmData.copy(
							dynamic = VirtualMachineDynamic(
									status = VirtualMachineStatus.Up,
									lastUpdated = System.currentTimeMillis(),
									id = vm.id,
									hostId = host.id,
									memoryUsed = vm.memory.min,
									cpuUsage = listOf()
							)
					)
				},
				hosts = state.hosts.update(host.id) {
					hostData ->
					hostData.copy(
							dynamic = hostDyn.copy(
									idleCpu = hostDyn.idleCpu, // TODO - estimate on the virtual machine CPU usage
									memFree = hostDyn.memFree, //TODO - estimate on memory usage of the VM
									memUsed = hostDyn.memUsed // TODO + estimate on memory usage of the VM
							)
					)
				}
		)
	}

	override fun reservations() = listOf<Reservation<*>>(
			VmReservation(vm = vm),
			HostMemoryReservation(reservedStorage = vm.memory.max, host = host)
	)

}
