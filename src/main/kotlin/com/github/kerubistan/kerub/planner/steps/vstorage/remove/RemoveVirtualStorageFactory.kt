package com.github.kerubistan.kerub.planner.steps.vstorage.remove

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.RecyclingStorageDevice
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object RemoveVirtualStorageFactory : AbstractOperationalStepFactory<RemoveVirtualStorage>() {

	override val problemHints = setOf<KClass<out Problem>>(RecyclingStorageDevice::class)
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<RemoveVirtualStorage> =
			state.vStorage.values
					.filter { it.stat.recycling && (it.dynamic?.allocations?.isEmpty()) ?: true }
					.map { RemoveVirtualStorage(it.stat) }
}