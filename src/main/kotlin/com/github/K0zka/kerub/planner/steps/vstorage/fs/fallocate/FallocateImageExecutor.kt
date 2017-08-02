package com.github.K0zka.kerub.planner.steps.vstorage.fs.fallocate

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.fs.rebase.FallocateImage
import com.github.K0zka.kerub.utils.junix.du.DU
import com.github.K0zka.kerub.utils.junix.fallocate.Fallocate
import java.math.BigInteger

class FallocateImageExecutor(
		private val exec: HostCommandExecutor,
		private val dynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<FallocateImage, BigInteger>() {
	override fun perform(step: FallocateImage) =
			exec.execute(step.host) {
				session ->
				val path = step.allocation.getPath(step.virtualStorage.id)
				Fallocate.dig(session, path)
				DU.du(session, path)
			}

	override fun update(step: FallocateImage, updates: BigInteger) {
		dynDao.update(id = step.virtualStorage.id) {
			it.copy(
					allocations = listOf(it.allocation.resize(updates))
			)
		}
	}
}