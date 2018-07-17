package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vm.match

/**
 * Takes each running virtual machines and running hosts except the one the VM is running on
 * and generates steps if the host matches the requirements of the VM.
 */
object KvmMigrateVirtualMachineFactory : AbstractOperationalStepFactory<KvmMigrateVirtualMachine>() {
	override fun produce(state: OperationalState): List<KvmMigrateVirtualMachine> {
		val runningVms = state.vms.values.filter {
			it.dynamic?.status == VirtualMachineStatus.Up
		}

		var steps = listOf<KvmMigrateVirtualMachine>()

		state.runningHosts.forEach {
			hostData ->
			runningVms.forEach {
				vmData ->
				if (match(hostData, vmData.stat)) {
					val sourceId = vmData.dynamic?.hostId
					val sourceHost = requireNotNull(state.hosts[sourceId])
					if (sourceId != hostData.stat.id) {
						steps += KvmMigrateVirtualMachine(vm = vmData.stat, source = sourceHost.stat, target = hostData.stat)
					}
				}
			}
		}

		return steps
	}
}