package com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateExecutor

class UnAllocateFsExecutor(
		private val hostExecutor : HostCommandExecutor,
		override val vssDynDao: VirtualStorageDeviceDynamicDao
) : AbstractUnAllocateExecutor<UnAllocateFs, VirtualStorageFsAllocation>() {
	override fun perform(step: UnAllocateFs): Unit = hostExecutor.execute(step.host) { session ->
		session.createSftpClient().use { sftpClient ->
			val fileName = step.allocation.getPath(step.vstorage.id)
			sftpClient.remove(fileName)
		}
	}

	override fun update(step: UnAllocateFs, updates: Unit) {
		vssDynDao.update(step.vstorage.id) {
			it.copy(
					allocations = it.allocations - step.allocation
			)
		}
		//TODO update host storage capability free space info
	}

}