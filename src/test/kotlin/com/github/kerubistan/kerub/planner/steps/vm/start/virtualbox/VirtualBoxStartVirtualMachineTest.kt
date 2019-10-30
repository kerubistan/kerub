package com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox

import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm

internal class VirtualBoxStartVirtualMachineTest : OperationalStepVerifications() {
	override val step = VirtualBoxStartVirtualMachine(
			host = testHost,
			vm = testVm
	)
}