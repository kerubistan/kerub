package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.vm.match

/**
 * Takes each running virtual machines and running hosts except the one the VM is running on
 * and generates steps if the host matches the requirements of the VM.
 */
public object MigrateVirtualMachineFactory : AbstractOperationalStepFactory<MigrateVirtualMachine>() {
	override fun produce(state: OperationalState): List<MigrateVirtualMachine> {
		val runningVms = state.vms.values.filter {
			state.vmDyns[it.id]?.status == VirtualMachineStatus.Up
		}
		val runningHosts = state.hosts.values.filter {
			state.hostDyns[it.id]?.status == HostStatus.Up
		}

		var steps = listOf<MigrateVirtualMachine>()

		runningHosts.forEach {
			host ->
			runningVms.forEach {
				vm ->
				if(match(host, state.hostDyns[host.id], vm)) {
					val sourceId = state.vmDyns[vm.id]?.hostId
					val sourceHost = state.hosts.getRaw(sourceId)!!
					if(sourceId != host.id) {
						steps += MigrateVirtualMachine(vm = vm, source = sourceHost, target = host)
					}
				}
			}
		}

		return steps
	}
}