package com.github.kerubistan.kerub.planner

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.utils.silent

class PlanRationalizerImpl(
		private val stepFactory: StepFactory<AbstractOperationalStep, Plan>,
		private val violationDetector: PlanViolationDetector = PlanViolationDetectorImpl,
		private val problemDetector: ProblemDetector<Problem> = CompositeProblemDetectorImpl
) : PlanRationalizer {

	/**
	 * Logic behind the rationalization process:
	 * - try to remove pairs of inverse steps (they recognize each other) - done
	 * - try to remove single steps and check if they still solve the problem - done
	 * - but this is not good enough because maybe we have sequences of irrelevant steps - TODO
	 */
	override fun rationalize(plan: Plan): Plan =
			if (plan.steps.size > 1) {
				val cleanup = tryRemoveInverses(plan)
				(1..(cleanup.steps.size - 1)).map {
					subPlan(cleanup, it)
				}.filterNotNull().minBy { it.steps.size } ?: cleanup
			} else plan

	internal fun tryRemoveInverses(plan: Plan): Plan {

		var work = plan

		plan.steps.mapIndexedNotNull { idx, step ->
			if (step is InvertibleStep) {
				plan.steps
						.subList(fromIndex = idx, toIndex = plan.steps.size)
						.firstOrNull { step.isInverseOf(it) }?.let { step to it }
			} else null
		}.forEach { (step, inverse) ->
			val testPlan = Plan.planBy(initial = work.states.first(), steps = work.steps - step - inverse)
			if (isTargetState(testPlan)) {
				work = testPlan
			}
		}
		return work
	}

	/**
	 * Create subplan
	 */
	private fun subPlan(plan: Plan, startStepIndex: Int): Plan? {
		val subSteps = plan.steps.subList(fromIndex = startStepIndex, toIndex = plan.steps.size)
		val targetPlan = silent {
			Plan.planBy(plan.states.first(), subSteps) { step, state ->
				stepFactory.produce(state).contains(step)
			}
		}
		return if (targetPlan != null && isTargetState(targetPlan)) targetPlan else null
	}

	private fun isTargetState(plan: Plan): Boolean =
			violationDetector.listViolations(plan).isEmpty()
					&& problemDetector.detect(plan).isEmpty()
}