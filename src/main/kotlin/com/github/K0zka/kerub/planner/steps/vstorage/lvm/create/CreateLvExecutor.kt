package com.github.K0zka.kerub.planner.steps.vstorage.lvm.create

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.insist
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.LvmLv

class CreateLvExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<CreateLv, LogicalVolume>() {

	override fun prepare(step: CreateLv) {
		val diskId = step.disk.id.toString()
		hostCommandExecutor.execute(step.host) {
			session ->

			require(!LvmLv.exists(session, volGroupName = step.volumeGroupName, volName = diskId)) {
				"Logical volume ${step.volumeGroupName} / ${diskId} already exists on host ${step.host.address}"
			}
		}

	}

	override fun update(step: CreateLv, updates: LogicalVolume) {
		hostCommandExecutor.execute(step.host, {
			session ->

			virtualDiskDynDao.add(
					VirtualStorageDeviceDynamic(
							id = step.disk.id,
							allocation = VirtualStorageLvmAllocation(
									hostId = step.host.id,
									actualSize = step.disk.size,
									path = updates.path
							)
					)
			)
		})
	}

	override fun perform(step: CreateLv): LogicalVolume =
			hostCommandExecutor.execute(step.host, {
				session ->
				LvmLv.create(session,
						vgName = step.volumeGroupName,
						name = step.disk.id.toString(),
						size = step.disk.size)

				//once created succesfully, take some effort to retrieve the data
				insist(3) {
					LvmLv.list(session, volGroupName = step.volumeGroupName, volName = step.disk.id.toString()).single()
				}
			})
}