package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.OperationalStateTransformation
import com.github.k0zka.finder4j.backtrack.Step
import com.github.k0zka.finder4j.backtrack.StepFactory

/**
 * Abstract baseclass for step factories.
 * Step factoryes should only care about the <strong>possibility</strong> of a step
 * not the <strong>feasibility</strong>
 */
abstract class AbstractOperationalStepFactory<S: Step<OperationalStateTransformation>> : StepFactory<S, OperationalStateTransformation> {
	//this here is only needed to have some restrictions on the method signature
	//like List and not MutableList and no null values allowed
	abstract fun produce(state: OperationalState): List<S>

	final override fun produce(state: OperationalStateTransformation): List<S> =
			produce(state.state)
}