package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.vm.base.AbstractForEachVmStepFactory
import kotlin.reflect.KClass

object PauseVirtualMachineFactory : AbstractForEachVmStepFactory<PauseVirtualMachine>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun filter(vmDyn: VirtualMachineDynamic): Boolean = vmDyn.status == VirtualMachineStatus.Up

	override fun create(vmData: VirtualMachineDataCollection, state: OperationalState): PauseVirtualMachine =
			PauseVirtualMachine(
					vm = vmData.stat,
					host = getHost(requireNotNull(vmData.dynamic?.hostId), state).stat
			)
}