package com.github.kerubistan.kerub.planner.steps.storage.block.copy

import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import io.github.kerubistan.kroki.collections.update

abstract class AbstractBlockCopy : AbstractOperationalStep {
	abstract val sourceDevice: VirtualStorageDevice
	abstract val targetDevice: VirtualStorageDevice
	abstract val sourceAllocation: VirtualStorageBlockDeviceAllocation
	abstract val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>

	/*
	this implementation only tells that after the step, the clone expectation will be cleared
	and the new allocation will be created, this is true regardless of the further details
	therefore the implementation can be in this abstract class
	*/
	override fun take(state: OperationalState): OperationalState = allocationStep.take(state).copy(
			vStorage = state.vStorage.update(targetDevice.id) { virtualStorage ->
				virtualStorage.copy(
						stat = virtualStorage.stat.copy(
								expectations = virtualStorage.stat.expectations
										.filterNot { it is CloneOfStorageExpectation }
						),
						dynamic = virtualStorage.updateDynamic { dyn ->
							dyn.copy(allocations = listOf(allocationStep.allocation))
						}
				)
			}
	)


	protected fun validate() {
		check(sourceDevice.id != targetDevice.id) {
			"source device (${sourceDevice.id}) must not be the same as the target device (${targetDevice.id})"
		}
	}

}