package com.github.kerubistan.kerub.planner.steps.vm.base

import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import java.util.UUID

abstract class AbstractForEachVmStepFactory<T : AbstractOperationalStep> : AbstractOperationalStepFactory<T>() {

	abstract fun filter(vmDyn: VirtualMachineDynamic): Boolean

	abstract fun create(vmData: VirtualMachineDataCollection, state: OperationalState): T

	protected fun getVm(id: UUID, state: OperationalState) =
			requireNotNull(state.vms[id]) { "VM not found with id $id" }

	protected fun getHost(id: UUID, state: OperationalState) =
			requireNotNull(state.hosts[id]) { "Host not found with id $id" }

	final override fun produce(state: OperationalState): List<T> {
		return state.vms.values
				.filter { it.dynamic != null && filter(it.dynamic) }
				.map { vm ->
					create(vm, state)
				}
	}
}