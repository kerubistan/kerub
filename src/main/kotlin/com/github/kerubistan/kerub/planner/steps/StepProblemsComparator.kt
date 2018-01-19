package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector

class StepProblemsComparator(private val detector: ProblemDetector<*> = CompositeProblemDetectorImpl, val plan: Plan) :
		Comparator<AbstractOperationalStep> {

	//this could have some smartness here
	private fun weight(problems: List<Problem>) = problems.size

	override fun compare(first: AbstractOperationalStep, second: AbstractOperationalStep): Int {
		val firstOutcome = first.take(plan)
		val secondOutcome = second.take(plan)

		val firstOutcomeProblems = detector.detect(firstOutcome).toList()
		val secondOutcomeProblems = detector.detect(secondOutcome).toList()

		val firstWeight = weight(firstOutcomeProblems)
		val secondWeight = weight(secondOutcomeProblems)

		return (firstWeight - secondWeight)
	}
}