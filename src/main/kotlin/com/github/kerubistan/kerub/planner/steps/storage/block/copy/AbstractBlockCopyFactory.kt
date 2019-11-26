package com.github.kerubistan.kerub.planner.steps.storage.block.copy

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvFactory
import io.github.kerubistan.kroki.collections.update
import kotlin.reflect.KClass

abstract class AbstractBlockCopyFactory<T : AbstractBlockCopy> : AbstractOperationalStepFactory<T>() {

	protected val allocationFactories = listOf(CreateLvFactory, CreateGvinumVolumeFactory)

	protected fun createUnallocatedState(state: OperationalState, targetStorage: VirtualStorageDataCollection) =
			state.copy(
					vStorage = state.vStorage.update(targetStorage.id) { targetStorageColl ->
						targetStorageColl.copy(
								stat = targetStorageColl.stat.copy(
										expectations = listOf(StorageAvailabilityExpectation())
								)
						)
					}
			)

	final override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf(CloneOfStorageExpectation::class)

	final override val problemHints: Set<KClass<out Problem>>
		get() = setOf()
}