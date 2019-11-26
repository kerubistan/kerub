package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFs
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFsFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory
import io.github.kerubistan.kroki.collections.concat

object MigrateFileAllocationFactory : AbstractMigrateAllocationFactory<MigrateFileAllocation>() {

	override val allocationFactories
			= listOf(TruncateImageFactory, CreateImageFactory)

	private val deAllocationFactories = listOf(UnAllocateFsFactory)

	override fun produce(state: OperationalState): List<MigrateFileAllocation> =
			listMigrateableVirtualDisks(state).map { candidateStorage ->
				// TODO this is not the right way to find out the source allocation format
				// the logic has to be transformed here
				// one does not migrate a virtual disk, one migrates an allocation of a virtual disk
				val format = candidateStorage.dynamic?.allocations?.map { it.type }?.toSet()?.singleOrNull()
				candidateStorage.dynamic?.allocations?.map { it.actualSize }
				val unAllocatedState =
						unallocatedState(state, candidateStorage,
								expectation = StorageAvailabilityExpectation(format = format ?: VirtualDiskFormat.raw))

				generateAllocationSteps(unAllocatedState, candidateStorage)
						.filter { format == null || it.format == format }
						.map { allocationStep ->
					generateDeAllocationSteps(state, candidateStorage)
							.map { deAllocationStep ->
						MigrateFileAllocation(
								sourceHost = deAllocationStep.host,
								deAllocationStep = deAllocationStep,
								allocationStep = allocationStep,
								targetHost = allocationStep.host,
								virtualStorage = candidateStorage.stat,
								sourceAllocation = deAllocationStep.allocation
						)
					}

				}
			}.concat().concat().filter { isSslKeyInstalled(it, state) }

	private fun generateDeAllocationSteps(
			state: OperationalState, candidateStorage: VirtualStorageDataCollection
	): List<UnAllocateFs> {
		val unallocationState = generteUnallocationState(state, candidateStorage)
		return deAllocationFactories.map { factory ->
			factory.produce(unallocationState)
		}.concat().filter { it.vstorage.id == candidateStorage.id }
	}

	private fun generateAllocationSteps(
			unAllocatedState: OperationalState,
			candidateStorage: VirtualStorageDataCollection
	): List<AbstractCreateVirtualStorage<VirtualStorageFsAllocation, FsStorageCapability>> =
			allocationFactories.map { it.produce(unAllocatedState) }.concat().filter { step ->
				candidateStorage.dynamic?.allocations?.none { it.hostId == step.allocation.hostId }
						?: false
			}.filter { targetAllocation ->
				filterAllocationSteps(candidateStorage, unAllocatedState, targetAllocation)
			}

}