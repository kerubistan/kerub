package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFs
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFsFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory
import io.github.kerubistan.kroki.collections.join

object MigrateFileAllocationFactory : AbstractMigrateAllocationFactory<MigrateFileAllocation>() {

	override val allocationFactories
			= listOf(TruncateImageFactory, CreateImageFactory)

	private val deAllocationFactories = listOf(UnAllocateFsFactory)

	override fun produce(state: OperationalState): List<MigrateFileAllocation> =
			listMigrateableVirtualDisks(state).map { candidateStorage ->
				val unAllocatedState = unallocatedState(state, candidateStorage)

				generateAllocationSteps(unAllocatedState, candidateStorage).map { allocationStep ->
					generateDeAllocationSteps(state, candidateStorage).map {
						MigrateFileAllocation(
								sourceHost = it.host,
								deAllocationStep = it,
								allocationStep = allocationStep,
								targetHost = allocationStep.host,
								virtualStorage = candidateStorage.stat,
								sourceAllocation = it.allocation
						)
					}

				}
			}.join().join()

	private fun generateDeAllocationSteps(
			state: OperationalState, candidateStorage: VirtualStorageDataCollection
	): List<UnAllocateFs> {
		val unallocationState = generteUnallocationState(state, candidateStorage)
		return deAllocationFactories.map { factory ->
			factory.produce(unallocationState)
		}.join().filter { it.vstorage.id == candidateStorage.id }
	}

	private fun generateAllocationSteps(
			unAllocatedState: OperationalState,
			candidateStorage: VirtualStorageDataCollection
	): List<AbstractCreateVirtualStorage<VirtualStorageFsAllocation, FsStorageCapability>> {
		return allocationFactories.map { it.produce(unAllocatedState) }.join().filter { step ->
			candidateStorage.dynamic?.allocations?.none { it.hostId == step.allocation.hostId }
					?: false
		}
	}


}