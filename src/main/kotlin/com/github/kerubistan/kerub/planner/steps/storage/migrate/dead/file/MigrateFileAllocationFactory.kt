package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory

object MigrateFileAllocationFactory : AbstractMigrateAllocationFactory<MigrateFileAllocation>() {
	override fun produce(state: OperationalState): List<MigrateFileAllocation> {
		TODO()
	}

	override val allocationFactories
			= listOf(TruncateImageFactory, CreateImageFactory)

}