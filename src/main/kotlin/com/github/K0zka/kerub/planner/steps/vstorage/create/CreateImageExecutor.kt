package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.junix.qemu.QemuImg
import java.math.BigInteger

class CreateImageExecutor(private val exec: HostCommandExecutor, private val dynDao: VirtualStorageDeviceDynamicDao) : AbstractStepExecutor<CreateImage>() {
	override fun perform(step: CreateImage) {
		exec.execute(step.host, {
			QemuImg.create(
					session = it,
					format = VirtualDiskFormat.raw, //TODO
					path = "/var/${step.device.id}", //TODO
					size = step.device.size
			)
		})
	}

	override fun update(step: CreateImage) {
		dynDao.add(VirtualStorageDeviceDynamic(
				id = step.device.id,
				allocation = VirtualStorageFsAllocation(
						hostId = step.host.id,
						mountPoint = step.path,
						type = step.format
				),
				actualSize = BigInteger.ZERO
		))
	}
}