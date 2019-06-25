package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.AbstractBlockCopyFactory
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvFactory
import com.github.kerubistan.kerub.utils.update
import io.github.kerubistan.kroki.collections.join

object LocalBlockCopyFactory : AbstractBlockCopyFactory<LocalBlockCopy>() {

	private val allocationFactories = listOf(CreateLvFactory, CreateGvinumVolumeFactory)

	override fun produce(state: OperationalState): List<LocalBlockCopy> =
			state.index.storageCloneRequirement.map { targetStorage ->
				val cloneOfStorageExpectation =
						targetStorage.stat.expectations.filterIsInstance<CloneOfStorageExpectation>().single()
				val sourceStorage = state.vStorage.getValue(cloneOfStorageExpectation.sourceStorageId)
				val updatedState = state.copy(
						vStorage = state.vStorage.update(targetStorage.id) { targetStorageColl ->
							targetStorageColl.copy(
									stat = targetStorageColl.stat.copy(
											expectations = listOf(StorageAvailabilityExpectation())
									)
							)
						}
				)
				sourceStorage.dynamic!!.allocations.filterIsInstance<VirtualStorageBlockDeviceAllocation>()
						.map { sourceAllocation ->
							allocationFactories.map { allocationFactory ->
								allocationFactory.produce(updatedState).map { allocationStep ->
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