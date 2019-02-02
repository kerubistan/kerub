package com.github.kerubistan.kerub.planner.steps.base

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor

abstract class AbstractUnAllocateExecutor<T : AbstractUnAllocate<S>, S : VirtualStorageAllocation>
	: AbstractStepExecutor<T, Unit>() {

	protected abstract val vssDynDao: VirtualStorageDeviceDynamicDao

	override fun update(step: T, updates: Unit) {
		vssDynDao.update(step.vstorage.id) {
			transformVirtualStorageDynamic(it, step)
		}
	}

	fun transformVirtualStorageDynamic(it: VirtualStorageDeviceDynamic, step: T): VirtualStorageDeviceDynamic {
		return it.copy(
				allocations = it.allocations - step.allocation
		)
	}
}