package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.HostMemoryReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep
import com.github.kerubistan.kerub.utils.update

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
