package com.github.kerubistan.kerub.planner.steps.storage.fs.fallocate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.du.DU
import com.github.kerubistan.kerub.utils.junix.fallocate.Fallocate
import java.math.BigInteger

class FallocateImageExecutor(
		private val exec: HostCommandExecutor,
		private val dynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<FallocateImage, BigInteger>() {
	override fun perform(step: FallocateImage) =
			exec.execute(step.host) { session ->
				val path = step.allocation.getPath(step.virtualStorage.id)
				Fallocate.dig(session, path)
				DU.du(session, path)
			}

	override fun update(step: FallocateImage, updates: BigInteger) {
		dynDao.update(id = step.virtualStorage.id) {
			it.copy(
					allocations = it.allocations + step.allocation.copy(
							actualSize = updates
					)
			)
		}
	}
}