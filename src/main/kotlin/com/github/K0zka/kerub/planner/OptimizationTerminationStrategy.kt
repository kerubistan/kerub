package com.github.K0zka.kerub.planner

import com.github.k0zka.finder4j.backtrack.TerminationStrategy

/**
 * At optimization, we do not want to stop at the first solution, but wait for better solutions to come
 * and terminate when the solutions do not improve for a while
 */
public class OptimizationTerminationStrategy : TerminationStrategy<Plan> {
	override fun stop(state: Plan): Boolean {
		//TODO
		throw UnsupportedOperationException()
	}
}