package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.qemu.ImageInfo

abstract class AbstractCreateImageExecutor<T : AbstractCreateImage>(
		protected val exec: HostCommandExecutor,
		protected val dynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<T, ImageInfo>() {

	override fun update(step: T, updates: ImageInfo) {
		dynDao.add(
				VirtualStorageDeviceDynamic(
						id = step.disk.id,
						allocations = listOf(
								createAllocation(step, updates)
						)
				))
	}

	internal abstract fun createAllocation(step: T, updates: ImageInfo): VirtualStorageFsAllocation


}