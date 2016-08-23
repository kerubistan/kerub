package com.github.K0zka.kerub.planner.steps.vm.migrate.kvm

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.vm.match
import com.github.K0zka.kerub.planner.steps.vm.storageAllocationMap

/**
 * Takes each running virtual machines and running hosts except the one the VM is running on
 * and generates steps if the host matches the requirements of the VM.
 */
object KvmMigrateVirtualMachineFactory : AbstractOperationalStepFactory<KvmMigrateVirtualMachine>() {
	override fun produce(state: OperationalState): List<KvmMigrateVirtualMachine> {
		val runningVms = state.vms.values.filter {
			state.vmDyns[it.id]?.status == VirtualMachineStatus.Up
		}
		val runningHosts = state.hosts.values.filter {
			state.hostDyns[it.id]?.status == HostStatus.Up
		}

		var steps = listOf<KvmMigrateVirtualMachine>()

		runningHosts.forEach {
			host ->
			runningVms.forEach {
				vm ->
				if (match(host, state.hostDyns[host.id], vm, storageAllocationMap(state))) {
					val sourceId = state.vmDyns[vm.id]?.hostId
					val sourceHost = requireNotNull(state.hosts[sourceId])
					if (sourceId != host.id) {
						steps += KvmMigrateVirtualMachine(vm = vm, source = sourceHost, target = host)
					}
				}
			}
		}

		return steps
	}
}