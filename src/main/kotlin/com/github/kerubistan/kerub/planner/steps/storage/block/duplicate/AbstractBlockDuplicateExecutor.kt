package com.github.kerubistan.kerub.planner.steps.storage.block.duplicate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh

abstract class AbstractBlockDuplicateExecutor<T : AbstractBlockDuplicate<*>>: AbstractStepExecutor<T, Unit>() {

	abstract val hostCommandExecutor: HostCommandExecutor
	abstract val virtualStorageDynamicDao: VirtualStorageDeviceDynamicDao

	override fun perform(step: T) {
		allocate(step)
		hostCommandExecutor.execute(step.sourceHost) {
			OpenSsh.copyBlockDevice(
					session = it,
					sourceDevice = step.source.getPath(step.vStorageDevice.id),
					targetAddress = step.targetHost.address,
					targetDevice = step.target.getPath(step.vStorageDevice.id)
			)
		}
	}

	abstract fun allocate(step: T)

	override fun update(step: T, updates: Unit) {
		virtualStorageDynamicDao.update(step.vStorageDevice.id) {
			it.copy(
					allocations = it.allocations + step.target
			)
		}
	}

}