package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.du.DU
import com.github.kerubistan.kerub.utils.junix.file.Rm
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import java.math.BigInteger

class InPlaceConvertImageExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualStorageDynamicDao: VirtualStorageDeviceDynamicDao)
	: AbstractStepExecutor<InPlaceConvertImage, Pair<String, BigInteger>>() {

	companion object {
		private val logger = getLogger(InPlaceConvertImageExecutor::class)
	}

	override fun perform(step: InPlaceConvertImage) =
		hostCommandExecutor.execute(step.host) {session ->
			val sourcePath = step.sourceAllocation.getPath(step.virtualStorage.id)
			val targetPath = sourcePath.replaceAfter(step.virtualStorage.id.toString(), ".${step.targetFormat}")
			QemuImg.convert(
					session = session,
					path = sourcePath,
					targetPath = targetPath,
					targetFormat = step.targetFormat
			)
			logger.info("done with conversion to $targetPath, checking")
			QemuImg.check(session, path = targetPath, format = step.targetFormat)
			logger.info("done checking $targetPath, moving to $sourcePath")
			Rm.remove(session, sourcePath)

			targetPath to DU.du(session, targetPath)
		}

	override fun update(step: InPlaceConvertImage, updates: Pair<String, BigInteger>) {
		virtualStorageDynamicDao.update(step.virtualStorage.id) {
			it.copy(
					allocations = it.allocations.map {
						allocation ->
						if(allocation == step.sourceAllocation) {
							step.sourceAllocation.copy(
									type = step.targetFormat,
									fileName = updates.first,
									actualSize = updates.second
							)
						} else allocation
					}
			)
		}
	}
}