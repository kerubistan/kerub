package com.github.kerubistan.kerub.planner.issues.violations.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.violations.VStorageViolationDetector

object NotSameStorageExpectationViolationDetector : VStorageViolationDetector<NotSameStorageExpectation> {
	override fun check(entity: VirtualStorageDevice, expectation: NotSameStorageExpectation,
					   state: OperationalState): Boolean {
		fun VirtualStorageDeviceDynamic.hostIds() = this.allocations.map { it.hostId }
		val diskDyn = state.vStorage[entity.id]?.dynamic
		return diskDyn == null || expectation.otherDiskId.let { otherVdiskId ->
			val otherDiskDyn = state.vStorage[otherVdiskId]?.dynamic
			otherDiskDyn == null
					|| otherDiskDyn.allocations.isEmpty()
					|| diskDyn.allocations.isEmpty()
					|| !diskDyn.hostIds().containsAll(otherDiskDyn.hostIds())
		}

	}
}