package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.steps.storage.lvm.base.AbstractAllocateStorageExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

abstract class AbstractCreateLvExecutor<T : AbstractCreateLv>(
		protected val hostCommandExecutor: HostCommandExecutor,
		virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractAllocateStorageExecutor<T, LogicalVolume>(virtualDiskDynDao) {

	override fun update(step: T, updates: LogicalVolume) {
		virtualDiskDynDao.update(step.disk.id,
				retrieve = { id ->
					virtualDiskDynDao[id] ?: VirtualStorageDeviceDynamic(id = id, allocations = listOf())
				}) {
			transformVirtualStorageDynamic(it, step, updates)
		}
	}

	fun transformVirtualStorageDynamic(dyn: VirtualStorageDeviceDynamic, step: T, updates: LogicalVolume): VirtualStorageDeviceDynamic {
		return dyn.copy(
				allocations = dyn.allocations + step.allocation.copy(
						actualSize = updates.size,
						path = updates.path
				)
		)
	}

	override fun prepare(step: T) {
		val diskId = step.disk.id.toString()
		hostCommandExecutor.execute(step.host) { session ->

			require(!LvmLv.exists(session, volGroupName = step.volumeGroupName, volName = diskId)) {
				"Logical volume ${step.volumeGroupName} / $diskId already exists on host ${step.host.address}"
			}
		}

	}


}