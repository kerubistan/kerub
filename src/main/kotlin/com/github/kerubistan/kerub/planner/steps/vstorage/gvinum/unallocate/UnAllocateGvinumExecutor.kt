package com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.unallocate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.gvinum.GVinum

class UnAllocateGvinumExecutor(private val hostExecutor : HostCommandExecutor,
							   override val vssDynDao: VirtualStorageDeviceDynamicDao)
	: AbstractUnAllocateExecutor<UnAllocateGvinum, VirtualStorageGvinumAllocation>() {
	override fun perform(step: UnAllocateGvinum) {
		hostExecutor.execute(step.host) {
			GVinum.removeVolume(it, step.vstorage.id.toString())
		}
	}
}