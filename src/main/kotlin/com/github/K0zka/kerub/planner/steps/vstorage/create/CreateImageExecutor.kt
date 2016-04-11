package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.qemu.QemuImg
import java.math.BigInteger

class CreateImageExecutor(private val exec: HostCommandExecutor, private val dynDao: VirtualStorageDeviceDynamicDao) : AbstractStepExecutor<CreateImage>() {

	companion object {
		private val logger = getLogger(CreateImageExecutor::class)
	}

	override fun perform(step: CreateImage) {
		exec.execute(step.host, {
			logger.info("Creating virtual disk {} on host {}", step.disk, step.host.address)
			QemuImg.create(
					session = it,
					format = VirtualDiskFormat.raw, //TODO
					path = "${step.path}/${step.disk.id}",
					size = step.disk.size
			)
			logger.info("Created virtual disk")
		})
	}

	override fun update(step: CreateImage) {
		dynDao.add(VirtualStorageDeviceDynamic(
				id = step.disk.id,
				allocation = VirtualStorageFsAllocation(
						hostId = step.host.id,
						mountPoint = step.path,
						type = step.format
				),
				actualSize = BigInteger.ZERO
		))
	}
}