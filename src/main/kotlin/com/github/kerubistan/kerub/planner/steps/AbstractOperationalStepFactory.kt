package com.github.kerubistan.kerub.planner.steps

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan

/**
 * Abstract baseclass for step factories.
 * Step factoryes should only care about the <strong>possibility</strong> of a step
 * not the <strong>feasibility</strong>
 */
abstract class AbstractOperationalStepFactory<S : AbstractOperationalStep> : StepFactory<S, Plan> {
	//this here is only needed to have some restrictions on the method signature
	//like List and not MutableList and no null values allowed
	abstract fun produce(state: OperationalState): List<S>

	final override fun produce(state: Plan): List<S> =
			produce(state.state)
}