package com.github.kerubistan.kerub.planner.steps.host.fence

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object FenceHostFactory : AbstractOperationalStepFactory<FenceHost>() {
	override val problemHints = setOf<KClass<out Problem>>()

	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<FenceHost> {
		throw UnsupportedOperationException()
	}

}