package com.github.kerubistan.kerub.planner.steps.vstorage.fs.rebase

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object RebaseImageFactory : AbstractOperationalStepFactory<FallocateImage>() {
	override fun produce(state: OperationalState): List<FallocateImage> {
		TODO("this depends on templates and multiple allocations")
	}
}