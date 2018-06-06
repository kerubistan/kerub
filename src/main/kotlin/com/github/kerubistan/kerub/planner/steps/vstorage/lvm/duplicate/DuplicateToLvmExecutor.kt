package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.duplicate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.steps.vstorage.block.duplicate.AbstractBlockDuplicateExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

class DuplicateToLvmExecutor(
		override val hostCommandExecutor: HostCommandExecutor,
		override val virtualStorageDynamicDao: VirtualStorageDeviceDynamicDao
) : AbstractBlockDuplicateExecutor<DuplicateToLvm>() {
	override fun allocate(step: DuplicateToLvm) {
		hostCommandExecutor.execute(step.targetHost) {
			LvmLv.create(session = it,
					vgName = step.target.vgName,
					size = step.vStorageDevice.size,
					name = step.vStorageDevice.id.toString()
			)
		}
	}
}