package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.vm.match
import com.github.K0zka.kerub.planner.steps.vm.storageAllocationMap

object KvmStartVirtualMachineFactory : AbstractOperationalStepFactory<KvmStartVirtualMachine>() {

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> {
		val vmsToRun = state.vms.values.filter {
			it.expectations.any {
				it is VirtualMachineAvailabilityExpectation
						&& it.up
			}
		}
		val vmsActuallyRunning = state.vmDyns.values.filter { it.status == VirtualMachineStatus.Up }.map { it.id }
		val vmsToStart = vmsToRun.filter { !vmsActuallyRunning.contains(it.id) }.filter { checkStartRequirements(it, state) }

		var steps = listOf<KvmStartVirtualMachine>()

		vmsToStart.forEach {
			vm ->
			val vstorageState = storageAllocationMap(state)
			state.hosts.values.forEach {
				host ->
				val dyn = state.hostDyns[host.id]
				if (match(host, dyn, vm, vstorageState)) {
					steps += KvmStartVirtualMachine(vm, host)
				}
			}
		}

		return steps
	}

	private fun checkStartRequirements(virtualMachine: VirtualMachine, state: OperationalState): Boolean {
		return allDisksAvailable(virtualMachine, state)
	}

	private fun allDisksAvailable(virtualMachine: VirtualMachine, state: OperationalState): Boolean =
			virtualMachine.virtualStorageLinks.all {
				link ->
				state.vStorageDyns[link.virtualStorageId] != null

			}

}