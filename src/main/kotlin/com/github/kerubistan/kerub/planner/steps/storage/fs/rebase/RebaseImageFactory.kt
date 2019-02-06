package com.github.kerubistan.kerub.planner.steps.storage.fs.rebase

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.fallocate.FallocateImage
import kotlin.reflect.KClass

object RebaseImageFactory : AbstractOperationalStepFactory<FallocateImage>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<FallocateImage> {
		TODO("this depends on templates and multiple allocations")
	}
}