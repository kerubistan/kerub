package com.github.kerubistan.kerub.planner.issues.violations.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.violations.VStorageViolationDetector

object NotSameStorageExpectationViolationDetector : VStorageViolationDetector<NotSameStorageExpectation> {
	override fun check(entity: VirtualStorageDevice, expectation: NotSameStorageExpectation,
					   state: OperationalState): Boolean {
		val diskDyn = state.vStorage[entity.id]?.dynamic
		return diskDyn == null || expectation.otherDiskId.let {
			otherVdiskId ->
			val otherDiskDyn = state.vStorage[otherVdiskId]?.dynamic
			otherDiskDyn == null || otherDiskDyn.allocation.hostId != diskDyn.allocation.hostId
		}

	}
}