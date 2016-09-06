package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.collection.HostDataCollection
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

abstract class AbstractStartVmFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>() {

	fun getWorkingHosts(state: OperationalState, filter: (HostDataCollection) -> Boolean): List<HostDataCollection> {
		return state.hosts.values.map {
			hostData ->
			if (hostData.dynamic != null && hostData.dynamic.status == HostStatus.Up && filter(hostData))
				hostData
			else
				null
		}.filterNotNull()
	}

	fun getWorkingHosts(state: OperationalState): List<HostDataCollection> = getWorkingHosts(state) { true }

	fun getVmsToStart(state: OperationalState, filter: (VirtualMachine) -> Boolean): List<VirtualMachine> {
		val vmsToRun = state.vms.values.filter {
			vm ->
			vm.stat.expectations.any {
				it is VirtualMachineAvailabilityExpectation
						&& it.up
			}
					&& filter(vm.stat)
		}
		val vmsActuallyRunning = state.vms.values.filter { it.dynamic?.status == VirtualMachineStatus.Up }.map { it.stat.id }
		val vmsToStart = vmsToRun.filter { !vmsActuallyRunning.contains(it.stat.id) }.filter { checkStartRequirements(it.stat, state) }
		return vmsToStart.map { it.stat }
	}

	fun getVmsToStart(state: OperationalState): List<VirtualMachine> = getVmsToStart(state) { true }

	private fun checkStartRequirements(virtualMachine: VirtualMachine, state: OperationalState): Boolean {
		return allDisksAvailable(virtualMachine, state)
	}

	private fun allDisksAvailable(virtualMachine: VirtualMachine, state: OperationalState): Boolean =
			virtualMachine.virtualStorageLinks.all {
				link ->
				state.vStorage[link.virtualStorageId]?.dynamic != null

			}

}