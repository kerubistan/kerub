package com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert.inplace

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object InPlaceConvertImageFactory : AbstractOperationalStepFactory<InPlaceConvertImage>() {
	override fun produce(state: OperationalState): List<InPlaceConvertImage> {
		TODO()
	}

	override val problemHints: Set<KClass<out Problem>>
		get() = TODO("not implemented")
	override val expectationHints: Set<KClass<out Expectation>>
		get() = TODO("not implemented")
}