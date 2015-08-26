package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.k0zka.finder4j.backtrack.Step

abstract class AbstractOperationalStep : Step<OperationalState> {
	/**
	 * Get the list of costs expected at executing this step.
	 */
	fun getCost() : List<Cost> = listOf();
}