package com.github.K0zka.kerub.planner.steps.vm.resume

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.collection.VirtualMachineDataCollection
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vm.base.AbstractForEachVmStepFactory

object ResumeVirtualMachineFactory : AbstractForEachVmStepFactory<ResumeVirtualMachine>() {
	override fun filter(vmDyn: VirtualMachineDynamic): Boolean
			= vmDyn.status == VirtualMachineStatus.Paused

	override fun create(vmData: VirtualMachineDataCollection, state: OperationalState): ResumeVirtualMachine
			= ResumeVirtualMachine(
			vm = vmData.stat,
			host = getHost(requireNotNull(vmData.dynamic).hostId, state).stat
	)
}