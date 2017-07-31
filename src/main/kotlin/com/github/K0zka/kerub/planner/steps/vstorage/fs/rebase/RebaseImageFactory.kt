package com.github.K0zka.kerub.planner.steps.vstorage.fs.rebase

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object RebaseImageFactory : AbstractOperationalStepFactory<FallocateImage>() {
	override fun produce(state: OperationalState): List<FallocateImage> {
		TODO("this depends on templates and multiple allocations")
	}
}