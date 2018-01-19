package com.github.kerubistan.kerub.planner.issues.violations.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.violations.VStorageViolationDetector

object StorageAvailabilityExpectationViolationDetector : VStorageViolationDetector<StorageAvailabilityExpectation> {
	override fun check(entity: VirtualStorageDevice, expectation: StorageAvailabilityExpectation,
					   state: OperationalState): Boolean =
			//if storage dynamic exists, allocation must exist
			state.vStorage[entity.id]?.dynamic != null
}