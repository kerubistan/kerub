package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

abstract class AbstractCreateLvExecutor<T : AbstractCreateLv>(
		protected val hostCommandExecutor: HostCommandExecutor,
		protected val virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<T, LogicalVolume>() {

	override fun update(step: T, updates: LogicalVolume) {
		virtualDiskDynDao.add(
				VirtualStorageDeviceDynamic(
						id = step.disk.id,
						allocations = listOf(VirtualStorageLvmAllocation(
								hostId = step.host.id,
								actualSize = updates.size,
								path = updates.path,
								vgName = step.volumeGroupName
						))
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