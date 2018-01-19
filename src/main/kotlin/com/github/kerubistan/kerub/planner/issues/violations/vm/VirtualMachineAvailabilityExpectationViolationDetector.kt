package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.violations.VmViolationDetector

object VirtualMachineAvailabilityExpectationViolationDetector : VmViolationDetector<VirtualMachineAvailabilityExpectation> {
	override fun check(entity: VirtualMachine,
					   expectation: VirtualMachineAvailabilityExpectation,
					   state: OperationalState): Boolean
			= state.isVmRunning(entity) == expectation.up
}