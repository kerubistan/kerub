package com.github.kerubistan.kerub.planner.issues.violations.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.StorageRedundancyExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.violations.VStorageViolationDetector

object StorageRedundancyExpectationViolationDetector : VStorageViolationDetector<StorageRedundancyExpectation> {

	override fun check(
			entity: VirtualStorageDevice,
			expectation: StorageRedundancyExpectation,
			state: OperationalState
	) =
			if (expectation.outOfBox) {
				checkOutOfTheBox(entity, expectation, state)
			} else {
				checkLocal(entity, expectation, state)
			}

	private fun checkLocal(
			entity: VirtualStorageDevice,
			expectation: StorageRedundancyExpectation,
			state: OperationalState
	) =
			state.vStorage[entity.id]?.dynamic?.allocations?.let {
				it.sumBy {
					it.getRedundancyLevel()
							.toInt() + 1
				} - 1
			} ?: 0 >= expectation.nrOfCopies

	/*
	 This may not be correct when doing gluster or ceph, because there it will probably be just a single allocation
	 while it will still have replicas on several boxes
	 */
	private fun checkOutOfTheBox(
			entity: VirtualStorageDevice,
			expectation: StorageRedundancyExpectation,
			state: OperationalState
	) = state.vStorage[entity.id]?.dynamic?.allocations?.distinctBy { it.hostId }?.size ?: 0 >= expectation.nrOfCopies

}