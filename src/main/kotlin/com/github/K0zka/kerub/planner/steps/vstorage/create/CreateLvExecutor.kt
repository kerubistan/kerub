package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.K0zka.kerub.utils.only

class CreateLvExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<CreateLv>() {

	override fun update(step: CreateLv) {
		hostCommandExecutor.execute(step.host, {
			session ->

			val lv = LvmLv.list(session, volGroupName = step.volumeGroupName, volName = step.disk.id.toString()).only()

			virtualDiskDynDao.add(
					VirtualStorageDeviceDynamic(
							id = step.disk.id,
							actualSize = step.disk.size,
							allocation = VirtualStorageLvmAllocation(
									hostId = step.host.id,
									path = lv.path
							)
					)
			)
		})
	}

	override fun perform(step: CreateLv) {
		hostCommandExecutor.execute(step.host, {
			session ->
			LvmLv.create(session,
					vgName = step.volumeGroupName,
					name = step.disk.id.toString(),
					size = step.disk.size)
		})
	}
}