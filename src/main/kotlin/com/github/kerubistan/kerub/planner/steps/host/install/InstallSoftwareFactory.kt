package com.github.kerubistan.kerub.planner.steps.host.install

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object InstallSoftwareFactory : AbstractOperationalStepFactory<InstallSoftware>() {
	override val problemHints= setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<InstallSoftware> {
		throw UnsupportedOperationException()
	}
}