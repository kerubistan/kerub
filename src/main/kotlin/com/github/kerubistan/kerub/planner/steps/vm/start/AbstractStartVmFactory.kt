package com.github.kerubistan.kerub.planner.steps.vm.start

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.containsAny
import com.github.kerubistan.kerub.utils.hasAny

abstract class AbstractStartVmFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>() {

	fun getWorkingHosts(state: OperationalState, filter: (HostDataCollection) -> Boolean): List<HostDataCollection> =
		state.hosts.values.mapNotNull {
			hostData ->
			if (hostData.dynamic != null && hostData.dynamic.status == HostStatus.Up && filter(hostData))
				hostData
			else
				null
		}

	fun getWorkingHosts(state: OperationalState): List<HostDataCollection> = getWorkingHosts(state) { true }

	private fun getVmsToStart(state: OperationalState, filter: (VirtualMachine) -> Boolean): List<VirtualMachine> {
		val vmsToRun = state.vms.values.filter {
			vm ->
			vm.stat.expectations.hasAny<VirtualMachineAvailabilityExpectation> { it.up }
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

	fun isHwVirtualizationSupported(host: Host) =
			host.capabilities?.cpus?.all {
				it.flags.containsAny("svm", "vmx")
			} ?: false


}