package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.qemu.ImageInfo
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import java.math.BigInteger

class CreateImageExecutor(exec: HostCommandExecutor,dynDao: VirtualStorageDeviceDynamicDao) :
		AbstractCreateImageExecutor<CreateImage>(exec, dynDao) {

	companion object {
		private val logger = getLogger(CreateImageExecutor::class)
	}

	override fun createAllocation(step: CreateImage, updates: ImageInfo): VirtualStorageFsAllocation =
			VirtualStorageFsAllocation(
					hostId = step.host.id,
					actualSize = BigInteger.valueOf(updates.diskSize),
					mountPoint = step.path,
					type = step.format,
					fileName = step.allocation.getPath(step.disk.id),
					capabilityId = step.capability.id
			)

	override fun perform(step: CreateImage) =
			exec.execute(step.host) {
				logger.info(
						"Creating virtual disk {}/{} on host {}, format: {}",
						step.path,
						step.disk.id,
						step.host.address,
						step.format
				)
				QemuImg.create(
						session = it,
						format = step.format,
						path = step.allocation.getPath(step.disk.id),
						size = step.disk.size
				)
				logger.info("Created virtual disk")
				QemuImg.info(session = it, path = step.allocation.getPath(step.disk.id))
			}

}