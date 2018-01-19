package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.kerubistan.kerub.planner.OperationalState

object ChassisManufacturerExpectationViolationDetector : AbstractVmHostViolationDetector<ChassisManufacturerExpectation>() {
	override fun checkWithHost(entity: VirtualMachine,
							   expectation: ChassisManufacturerExpectation,
							   state: OperationalState,
							   host: Host): Boolean
			= host.capabilities?.chassis?.manufacturer == expectation.manufacturer
}