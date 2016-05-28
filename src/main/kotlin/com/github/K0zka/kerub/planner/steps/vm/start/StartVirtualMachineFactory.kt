package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.vm.match

object StartVirtualMachineFactory : AbstractOperationalStepFactory<StartVirtualMachine>() {

	override fun produce(state: OperationalState): List<StartVirtualMachine> {
		val vmsToRun = state.vms.values.filter {
			it.expectations.any {
				it is VirtualMachineAvailabilityExpectation
						&& it.up
			}
		}
		val vmsActuallyRunning = state.vmDyns.values.filter { it.status == VirtualMachineStatus.Up }.map { it.id }
		val vmsToStart = vmsToRun.filter { !vmsActuallyRunning.contains(it.id) }.filter { checkStartRequirements(it, state) }

		var steps = listOf<StartVirtualMachine>()

		vmsToStart.forEach {
			vm ->
			state.hosts.values.forEach {
				host ->
				val dyn = state.hostDyns[host.id]
				if (match(host, dyn, vm)) {
					steps += StartVirtualMachine(vm, host)
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