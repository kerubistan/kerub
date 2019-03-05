package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolume
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeExecutor
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinum
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinumExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLvExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLvExecutor
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume

class MigrateBlockAllocationExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val vssDynDao: VirtualStorageDeviceDynamicDao,
		private val hostDynDao: HostDynamicDao
) : AbstractStepExecutor<MigrateBlockAllocation, Pair<Any, Any>>() {

	private val logger = getLogger(MigrateBlockAllocationExecutor::class)

	private val createLvExecutor = CreateLvExecutor(hostCommandExecutor, vssDynDao)
	private val createGvinumExecutor = CreateGvinumVolumeExecutor(hostCommandExecutor, vssDynDao, hostDynDao)
	private val createThinLvExecutor = CreateThinLvExecutor(hostCommandExecutor, vssDynDao)
	private val unAllocateLvExecutor = UnAllocateLvExecutor(hostCommandExecutor, vssDynDao)
	private val unAllocateGvinumExecutor = UnAllocateGvinumExecutor(hostCommandExecutor, vssDynDao)

	override fun perform(step: MigrateBlockAllocation): Pair<Any, Any> {
		logger.info("creating new allocation on {}", step.allocationStep.host)
		val allocationResult = when (step.allocationStep) {
			is CreateLv -> createLvExecutor.perform(step.allocationStep)
			is CreateGvinumVolume -> createGvinumExecutor.perform(step.allocationStep)
			is CreateThinLv -> createThinLvExecutor.perform(step.allocationStep)
			else -> TODO("Allocation step type not handled: ${step.allocationStep}")
		}
		logger.info("copying block device from {} to {}", step.sourceHost.address, step.targetHost.address)
		hostCommandExecutor.execute(step.sourceHost) {
			OpenSsh.copyBlockDevice(
					session = it,
					sourceDevice = step.sourceAllocation.getPath(step.virtualStorage.id),
					targetAddress = step.targetHost.address,
					targetDevice = step.allocationStep.allocation.getPath(step.virtualStorage.id))
		}
		logger.info("removing allocation on {}", step.deAllocationStep.host)
		val deAllocationResult = when (step.deAllocationStep) {
			is UnAllocateGvinum -> unAllocateGvinumExecutor.perform(step.deAllocationStep)
			is UnAllocateLv -> unAllocateLvExecutor.perform(step.deAllocationStep)
			else -> TODO("De-allocation step type not handled: ${step.deAllocationStep}")
		}
		return allocationResult to deAllocationResult
	}

	override fun update(step: MigrateBlockAllocation, updates: Pair<Any, Any>) {
		val (allocationResult, _) = updates
		vssDynDao.update(step.virtualStorage.id) { dyn ->
			val update = when (step.deAllocationStep) {
				is UnAllocateGvinum -> unAllocateGvinumExecutor.transformVirtualStorageDynamic(
						dyn,
						step.deAllocationStep)
				is UnAllocateLv -> unAllocateLvExecutor.transformVirtualStorageDynamic(dyn, step.deAllocationStep)
				else -> TODO("De-allocation step type not handled: ${step.deAllocationStep}")
			}
			when (step.allocationStep) {
				is CreateLv -> createLvExecutor.transformVirtualStorageDynamic(
						update,
						step.allocationStep,
						allocationResult as LogicalVolume)
				is CreateGvinumVolume -> createGvinumExecutor.transformVirtualStorageDynamic(
						update,
						step.allocationStep)
				is CreateThinLv -> createThinLvExecutor.transformVirtualStorageDynamic(
						update,
						step.allocationStep,
						allocationResult as LogicalVolume)
				else -> TODO("Allocation step type not handled: ${step.allocationStep}")
			}

		}
	}
}