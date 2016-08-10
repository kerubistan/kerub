package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.match
import com.github.K0zka.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.K0zka.kerub.planner.steps.vm.storageAllocationMap
import com.github.K0zka.kerub.utils.join

object KvmStartVirtualMachineFactory : AbstractStartVmFactory<KvmStartVirtualMachine>() {

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> =
			getVmsToStart(state).map {
				vm ->
				getWorkingHosts(state).filter { match(it.key, it.value, vm, storageAllocationMap(state)) }.map {
					KvmStartVirtualMachine(vm, it.key)
				}
			}.join()

}