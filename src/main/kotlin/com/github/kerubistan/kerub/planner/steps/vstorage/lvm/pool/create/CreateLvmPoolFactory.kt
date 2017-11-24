package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.create

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object CreateLvmPoolFactory : AbstractOperationalStepFactory<CreateLvmPool>() {
	override fun produce(state: OperationalState): List<CreateLvmPool> {
		TODO()
	}
}