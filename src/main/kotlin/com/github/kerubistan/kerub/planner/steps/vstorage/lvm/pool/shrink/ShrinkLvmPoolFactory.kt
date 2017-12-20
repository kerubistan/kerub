package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.shrink

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object ShrinkLvmPoolFactory : AbstractOperationalStepFactory<ShrinkLvmPool>() {
	override fun produce(state: OperationalState): List<ShrinkLvmPool> {
		TODO()
	}
}