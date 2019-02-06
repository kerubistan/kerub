package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.du.DU
import com.github.kerubistan.kerub.utils.junix.file.Mv
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import java.math.BigInteger

class InPlaceConvertImageExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualStorageDynamicDao: VirtualStorageDeviceDynamicDao)
	: AbstractStepExecutor<InPlaceConvertImage, BigInteger>() {

	companion object {
		private val logger = getLogger(InPlaceConvertImageExecutor::class)
	}

	override fun perform(step: InPlaceConvertImage) =
		hostCommandExecutor.execute(step.host) {session ->
			val sourcePath = step.sourceAllocation.getPath(step.virtualStorage.id)
			val targetTempPath = "${sourcePath}_ipc_.${step.targetFormat}"
			QemuImg.convert(
					session = session,
					path = sourcePath,
					targetPath = targetTempPath,
					targetFormat = step.targetFormat
			)
			logger.info("done with conversion to $targetTempPath, checking")
			QemuImg.check(session, path = targetTempPath, format = step.targetFormat)
			logger.info("done checking $targetTempPath, moving to $sourcePath")
			Mv.move(session, targetTempPath, sourcePath)

			DU.du(session, sourcePath)
		}

	override fun update(step: InPlaceConvertImage, updates: BigInteger) {
		virtualStorageDynamicDao.update(step.virtualStorage.id) {
			it.copy(
					allocations = it.allocations.map {
						allocation ->
						if(allocation == step.sourceAllocation) {
							step.sourceAllocation.copy(
									type = step.targetFormat,
									actualSize = updates
							)
						} else allocation
					}
			)
		}
	}
}