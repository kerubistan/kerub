package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block.MigrateBlockAllocationFactory

object MigrateFileAllocationFactory : AbstractMigrateAllocationFactory<MigrateFileAllocation>() {

	override fun produce(state: OperationalState): List<MigrateFileAllocation> =
		state.vStorage.values.filter { MigrateBlockAllocationFactory.canMigrate(it, state) }.map { candidateStorage ->
			val unAllocatedState = unallocatedState(state, candidateStorage)

			TODO()
		}

	override val allocationFactories
			= listOf(TruncateImageFactory, CreateImageFactory)

}