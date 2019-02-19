package com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.VolumeGroup
import com.github.kerubistan.kerub.utils.update

class MirrorVolumeExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostDynamicDao: HostDynamicDao,
		private val virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<MirrorVolume, Pair<LogicalVolume, VolumeGroup>>() {
	@ExperimentalUnsignedTypes
	override fun perform(step: MirrorVolume): Pair<LogicalVolume, VolumeGroup> =
			hostCommandExecutor.execute(step.host) {
				LvmLv.mirror(
						session = it,
						volumeName = step.vStorage.idStr,
						vgName = step.allocation.vgName,
						mirrors = step.mirrors)
				LvmLv.list(
						session = it,
						volGroupName = step.allocation.vgName,
						volName = step.vStorage.idStr).single() to LvmVg.list(
						it,
						vgName = step.allocation.vgName).single()
			}

	@ExperimentalUnsignedTypes
	override fun update(step: MirrorVolume, updates: Pair<LogicalVolume, VolumeGroup>) {
		val (lv, vg) = updates
		hostDynamicDao.update(step.host.id) { dyn ->
			dyn.copy(
					storageStatus = dyn.storageStatus.update(
							selector = { it.id == step.capability.id },
							map = {
								(it as SimpleStorageDeviceDynamic).copy(
										freeCapacity = vg.freeSize
								)
							}
					)
			)
		}
		virtualStorageDeviceDynamicDao.update(step.vStorage.id) { dyn ->
			dyn.copy(
					allocations = dyn.allocations.update(
							selector = { it.capabilityId == step.capability.id },
							map = {
								(it as VirtualStorageLvmAllocation).copy(
										mirrors = step.mirrors.toByte(),
										actualSize = lv.size
								)
							}
					)
			)
		}
	}
}