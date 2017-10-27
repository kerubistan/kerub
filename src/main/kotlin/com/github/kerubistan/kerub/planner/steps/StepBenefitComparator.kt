package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.planner.OperationalState
import java.util.Comparator

class StepBenefitComparator(val state: OperationalState) : Comparator<AbstractOperationalStep> {
	override fun compare(first: AbstractOperationalStep, second: AbstractOperationalStep): Int {
		//TODO: issue #128
		// this may not be efficient, but at the moment there
		// is no other way than to really take the step
		// and compare the number of constraints broken
		// in the new states
		val firstOutcome = first.take(state)
		val secondOutcome = second.take(state)
		for (level in listOf(ExpectationLevel.DealBreaker, ExpectationLevel.Want, ExpectationLevel.Wish)) {
			val firstOutcomeIssues = firstOutcome.getNrOfUnsatisfiedExpectations(level)
			val secondOutcomeIssues = secondOutcome.getNrOfUnsatisfiedExpectations(level)
			if (firstOutcomeIssues != secondOutcomeIssues) {
				return firstOutcomeIssues - secondOutcomeIssues
			}
		}
		return 0
	}
}