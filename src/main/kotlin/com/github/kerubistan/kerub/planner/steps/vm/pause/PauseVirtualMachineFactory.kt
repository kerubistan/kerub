package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vm.base.AbstractForEachVmStepFactory

object PauseVirtualMachineFactory : AbstractForEachVmStepFactory<PauseVirtualMachine>() {

	override fun filter(vmDyn: VirtualMachineDynamic): Boolean
			= vmDyn.status == VirtualMachineStatus.Up

	override fun create(vmData: VirtualMachineDataCollection, state: OperationalState): PauseVirtualMachine =
			PauseVirtualMachine(
					vm = vmData.stat,
					host = getHost(requireNotNull(vmData.dynamic?.hostId), state).stat
			)
}