package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.match
import com.github.K0zka.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.K0zka.kerub.planner.steps.vm.storageAllocationMap

object KvmStartVirtualMachineFactory : AbstractStartVmFactory<KvmStartVirtualMachine>() {

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> {
		val vmsToStart = getVmsToStart(state)

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


}