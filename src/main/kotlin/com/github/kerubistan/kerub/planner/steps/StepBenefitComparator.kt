package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanViolationDetector
import io.github.kerubistan.kroki.collections.concat
import java.util.Comparator

class StepBenefitComparator(private val planViolationDetector: PlanViolationDetector, val plan: Plan) :
		Comparator<AbstractOperationalStep> {

	private fun listViolations(plan: Plan) =
			planViolationDetector.listViolations(plan).map { it.value }.concat()

	override fun compare(first: AbstractOperationalStep, second: AbstractOperationalStep): Int {
		//TODO: issue #128
		// this may not be efficient, but at the moment there
		// is no other way than to really take the step
		// and compare the number of constraints broken
		// in the new states
		val firstOutcome = first.take(plan)
		val secondOutcome = second.take(plan)
		val firstOutcomeViolations = listViolations(firstOutcome)

		val secondOutcomeViolations = listViolations(secondOutcome)

		//if any of these are empty, then we can skip the rest
		if (firstOutcomeViolations.isEmpty() || secondOutcomeViolations.isEmpty()) {
			return firstOutcomeViolations.size - secondOutcomeViolations.size
		}

		for (level in listOf(ExpectationLevel.DealBreaker, ExpectationLevel.Want, ExpectationLevel.Wish)) {
			val byLevel: (Expectation) -> Boolean = { it.level == level }
			val secondViolationsForLevel = secondOutcomeViolations.count(byLevel)
			val firstViolationsForLevel = firstOutcomeViolations.count(byLevel)
			if (firstViolationsForLevel != secondViolationsForLevel) {
				return firstViolationsForLevel - secondViolationsForLevel
			}
		}
		return 0
	}
}	