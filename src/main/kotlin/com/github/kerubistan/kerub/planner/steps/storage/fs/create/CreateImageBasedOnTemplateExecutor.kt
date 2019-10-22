package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.utils.junix.qemu.ImageInfo
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import java.math.BigInteger

class CreateImageBasedOnTemplateExecutor(
		exec: HostCommandExecutor,
		dynDao: VirtualStorageDeviceDynamicDao
) : AbstractCreateImageExecutor<CreateImageBasedOnTemplate>(exec, dynDao) {

	override fun createAllocation(step: CreateImageBasedOnTemplate, updates: ImageInfo): VirtualStorageFsAllocation =
			VirtualStorageFsAllocation(
					hostId = step.host.id,
					actualSize = BigInteger.valueOf(updates.diskSize),
					mountPoint = step.path,
					type = step.format,
					fileName = step.allocation.getPath(step.disk.id),
					capabilityId = step.capability.id,
					backingFile = step.baseAllocation.getPath(step.baseDisk.id)
			)

	override fun perform(step: CreateImageBasedOnTemplate): ImageInfo =
			exec.execute(step.host) { session ->
				QemuImg.create(
						session = session,
						format = step.format,
						path = step.allocation.getPath(step.disk.id),
						size = step.disk.size,
						backingFile = step.baseAllocation.getPath(step.baseDisk.id)
				)
				QemuImg.info(session, step.allocation.getPath(step.disk.id))
			}
}