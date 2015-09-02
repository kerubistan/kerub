package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.OperationalStateTransformation
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.k0zka.finder4j.backtrack.Step

abstract class AbstractOperationalStep : Step<OperationalStateTransformation> {

	/**
	 * Take an operational state transformation step
	 */
	abstract fun take(state: OperationalState): OperationalState

	/**
	 *
	 */
	final override fun take(state: OperationalStateTransformation): OperationalStateTransformation =
		OperationalStateTransformation(
				state = take(state.state),
		        steps = state.steps + this
		                              )

	/**
	 * Get the list of costs expected at executing this step.
	 */
	fun getCost() : List<Cost> = listOf();

}