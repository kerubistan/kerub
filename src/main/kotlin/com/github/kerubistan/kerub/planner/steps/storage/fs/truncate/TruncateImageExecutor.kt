package com.github.kerubistan.kerub.planner.steps.storage.fs.truncate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.truncate.Truncate

class TruncateImageExecutor(
		private val exec: HostCommandExecutor,
		private val dynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<TruncateImage, Unit>() {
	override fun perform(step: TruncateImage) =
			exec.execute(step.host) {
				Truncate.truncate(it, step.allocation.mountPoint + "/" + step.allocation.fileName, step.disk.size)
				//there is nothing to check here because on a successful completion the file is created and actual size is 0
			}

	override fun update(step: TruncateImage, updates: Unit) =
			dynDao.update(
					id = step.disk.id,
					retrieve = {
						dynDao[step.disk.id] ?: VirtualStorageDeviceDynamic(id = step.disk.id, allocations = listOf())
					}
			) {
				it.copy(
						allocations = it.allocations + step.allocation
				)
			}
}