package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.utils.insist
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

class CreateLvExecutor(
		hostCommandExecutor: HostCommandExecutor,
		virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractCreateLvExecutor<CreateLv>(hostCommandExecutor, virtualDiskDynDao) {

	override fun perform(step: CreateLv): LogicalVolume =
			hostCommandExecutor.execute(step.host) { session ->
				LvmLv.create(session,
						vgName = step.volumeGroupName,
						name = step.disk.id.toString(),
						size = step.disk.size)

				//once created succesfully, take some effort to retrieve the data
				insist(3) {
					LvmLv.list(session, volGroupName = step.volumeGroupName, volName = step.disk.id.toString()).single()
				}
			}
}