package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

class CreateThinLvExecutor(
		hostCommandExecutor: HostCommandExecutor,
		virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractCreateLvExecutor<CreateThinLv>(hostCommandExecutor, virtualDiskDynDao) {

	override fun perform(step: CreateThinLv): LogicalVolume =
			hostCommandExecutor.execute(step.host) {
				LvmLv.create(session = it, vgName = step.volumeGroupName, size = step.disk.size,
							 poolName = step.poolName,
							 name = step.disk.id.toString())
				LvmLv.list(session = it, volName = step.disk.id.toString())
						.single { it.name == step.disk.id.toString() }
			}

}