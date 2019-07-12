package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block

import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocate
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeFactory
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinumFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory
import io.github.kerubistan.kroki.collections.join

object MigrateBlockAllocationFactory : AbstractMigrateAllocationFactory<MigrateBlockAllocation>() {

	override val allocationFactories = listOf(CreateLvFactory, CreateThinLvFactory, CreateGvinumVolumeFactory)

	private val deAllocationFactories = listOf(UnAllocateLvFactory, UnAllocateGvinumFactory)

	/*
	idea: take each RW virtual storage
		(RO could be just duplicated, so I don't care about that here)
	let the allocation factories generate the target allocations
		filter out the allocations where the disk is already actually allocated on the same host
		(for storage we have to tell the allocation factory that it is not yet allocated)
	then let the de-allocation factories produce offers for de-allocation
		(we have to tell these factories that the storage is to be recycled)
	then let's filter for the combinations where the source host's key
		is installed on the target host
	 */
	override fun produce(state: OperationalState): List<MigrateBlockAllocation> =
			listMigrateableVirtualDisks(state).map { candidateStorage ->
				val unAllocatedState = unallocatedState(state, candidateStorage)

				generateAllocationSteps(candidateStorage, unAllocatedState).map { allocationStep ->
					generateDeAllocationSteps(state, candidateStorage).map {
						MigrateBlockAllocation(
								deAllocationStep = it,
								allocationStep = allocationStep,
								virtualStorage = candidateStorage.stat,
								compression = null,
								sourceAllocation = it.allocation as VirtualStorageBlockDeviceAllocation,
								sourceHost = it.host,
								targetHost = allocationStep.host
						)
					}

				}

			}.join().join().filter { isSslKeyInstalled(it, state) }

	private fun generateDeAllocationSteps(
			state: OperationalState,
			candidateStorage: VirtualStorageDataCollection
	): List<AbstractUnAllocate<*>> {
		val unallocationState = generteUnallocationState(state, candidateStorage)
		return deAllocationFactories.map { factory ->
			factory.produce(unallocationState)
		}.join().filter { it.vstorage.id == candidateStorage.id }
	}


	private fun generateAllocationSteps(
			candidateStorage: VirtualStorageDataCollection,
			unAllocatedState: OperationalState
	) = allocationFactories.map { it.produce(unAllocatedState) }.join().filter { step ->
		candidateStorage.dynamic?.allocations?.none { it.hostId == step.allocation.hostId }
				?: false
	}

}