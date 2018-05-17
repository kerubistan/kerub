package com.github.kerubistan.kerub.planner.steps.vstorage.remove

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object RemoveVirtualStorageFactory : AbstractOperationalStepFactory<RemoveVirtualStorage>() {
	override fun produce(state: OperationalState): List<RemoveVirtualStorage> =
			state.vStorage.values
					.filter { it.stat.recycling && (it.dynamic?.allocations?.isEmpty()) ?: true }
					.map { RemoveVirtualStorage(it.stat) }
}