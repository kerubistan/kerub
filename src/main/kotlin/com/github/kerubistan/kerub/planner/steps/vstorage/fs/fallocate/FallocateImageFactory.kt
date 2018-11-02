package com.github.kerubistan.kerub.planner.steps.vstorage.fs.fallocate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object FallocateImageFactory : AbstractOperationalStepFactory<FallocateImage>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<FallocateImage> {
		//pending: for this the aggregated history of the disk should be used
		// but that is not yet in operational state
		TODO()
	}
}