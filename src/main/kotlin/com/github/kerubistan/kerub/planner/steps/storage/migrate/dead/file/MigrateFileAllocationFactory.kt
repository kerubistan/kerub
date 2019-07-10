package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block.MigrateBlockAllocationFactory
import io.github.kerubistan.kroki.collections.join

object MigrateFileAllocationFactory : AbstractMigrateAllocationFactory<MigrateFileAllocation>() {

	override fun produce(state: OperationalState): List<MigrateFileAllocation> =
		state.vStorage.values.filter { MigrateBlockAllocationFactory.canMigrate(it, state) }.map { candidateStorage ->
			val unAllocatedState = unallocatedState(state, candidateStorage)

			allocationFactories.map { it.produce(unAllocatedState) }.join().filter { step ->
				candidateStorage.dynamic?.allocations?.none { it.hostId == step.allocation.hostId }
						?: false
			}.map { allocationStep ->

					}

					TODO()
		}

	override val allocationFactories
			= listOf(TruncateImageFactory, CreateImageFactory)


}