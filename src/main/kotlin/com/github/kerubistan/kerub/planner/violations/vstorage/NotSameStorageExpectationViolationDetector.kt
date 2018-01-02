package com.github.kerubistan.kerub.planner.violations.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.VirtualStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.violations.VStorageViolationDetector

object NotSameStorageExpectationViolationDetector : VStorageViolationDetector {
	override fun check(entity: VirtualStorageDevice, expectation: VirtualStorageExpectation, state: OperationalState): Boolean {
		TODO()
	}
}