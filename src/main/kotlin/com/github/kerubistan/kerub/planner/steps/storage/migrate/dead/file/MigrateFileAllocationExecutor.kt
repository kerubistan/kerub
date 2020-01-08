package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh

class MigrateFileAllocationExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
		private val stepExecutor: StepExecutor<AbstractOperationalStep>
) : AbstractStepExecutor<MigrateFileAllocation, Unit>() {
	override fun perform(step: MigrateFileAllocation) {
		stepExecutor.execute(step.allocationStep)
		hostCommandExecutor.execute(step.sourceHost) { session ->
			OpenSsh.copyBlockDevice(
					session = session,
					sourceDevice = step.sourceAllocation.getPath(step.virtualStorage.id),
					targetDevice = step.allocationStep.allocation.getPath(step.virtualStorage.id),
					targetAddress = step.targetHost.address
			)
			stepExecutor.execute(step.deAllocationStep)
			/*
				TODO
			    here we should get some device size info, which might be specific to the
			 	logical volume technology (sparse files, thin lv, gvinum, zfs volume, etc)
			 */
		}
	}

	override fun update(step: MigrateFileAllocation, updates: Unit) {
		/*
		 	TODO
		 	here too, we should update the host state based on the info retrieved by perform
		 */
		virtualStorageDeviceDynamicDao.update(step.virtualStorage.id) { dyn ->
			dyn.copy(
					allocations = dyn.allocations - step.sourceAllocation + step.allocationStep.allocation
			)
		}
	}
}