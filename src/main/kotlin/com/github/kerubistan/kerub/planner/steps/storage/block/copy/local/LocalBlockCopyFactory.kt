package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.AbstractBlockCopyFactory
import io.github.kerubistan.kroki.collections.join

object LocalBlockCopyFactory : AbstractBlockCopyFactory<LocalBlockCopy>() {

	override fun produce(state: OperationalState): List<LocalBlockCopy> =
			state.index.storageCloneRequirement.map { targetStorage ->
				val cloneOfStorageExpectation =
						targetStorage.stat.expectations.filterIsInstance<CloneOfStorageExpectation>().single()
				val sourceStorage = state.vStorage.getValue(cloneOfStorageExpectation.sourceStorageId)
				val unallocatedState = createUnallocatedState(state, targetStorage)
				sourceStorage.dynamic!!.allocations.filterIsInstance<VirtualStorageBlockDeviceAllocation>()
						.map { sourceAllocation ->
							allocationFactories.map { allocationFactory ->
								allocationFactory.produce(unallocatedState).map { allocationStep ->
									LocalBlockCopy(
											targetDevice = targetStorage.stat,
											sourceAllocation = sourceAllocation,
											sourceDevice = sourceStorage.stat,
											allocationStep = allocationStep
									)
								}
							}
						}
			}.join().join().join()

}