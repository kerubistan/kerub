package com.github.kerubistan.kerub.planner

import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy

class RationalizedFirstSolutionTerminationStrategy(private val rationalizer: PlanRationalizer) :
		FirstSolutionTerminationStrategy<Plan>() {
	override fun onSolution(state: Plan) {
		super.onSolution(
				rationalizer.rationalize(state))
	}
}
