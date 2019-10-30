package com.github.kerubistan.kerub.planner.steps.storage.remove

import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor

class RemoveVirtualStorageExecutor(private val vsdDao: VirtualStorageDeviceDao,
								   private val vsdDynDao: VirtualStorageDeviceDynamicDao)
	: AbstractStepExecutor<RemoveVirtualStorage, Unit>() {
	override fun perform(step: RemoveVirtualStorage) {
		vsdDao.remove(step.virtualStorage.id)
		vsdDynDao.remove(step.virtualStorage.id)
	}

	override fun update(step: RemoveVirtualStorage, updates: Unit) {
		//nothing left to do here, all done in perform
	}
}