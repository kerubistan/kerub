package com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

class UnAllocateLvExecutor(
		private val hostExecutor: HostCommandExecutor,
		override val vssDynDao: VirtualStorageDeviceDynamicDao
) : AbstractUnAllocateExecutor<UnAllocateLv, VirtualStorageLvmAllocation>() {
	override fun perform(step: UnAllocateLv) {
		hostExecutor.execute(step.host) { clientSession ->
			LvmLv.remove(session = clientSession, path = step.allocation.path)
		}
	}

}