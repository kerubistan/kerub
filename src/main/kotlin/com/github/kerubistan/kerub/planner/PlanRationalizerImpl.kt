package com.github.kerubistan.kerub.planner

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.utils.LogLevel
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
	 */
	override fun rationalize(plan: Plan): Plan =
			if (plan.steps.size > 1) {
				val cleanup = tryRemoveSingles(tryRemoveInverses(plan))
				(1..(cleanup.steps.size - 1)).map {
					subPlan(cleanup, it)
				}.filterNotNull().minBy { it.steps.size } ?: cleanup
			} else plan

	private fun simplify(plan: Plan, generator: (OperationalState, List<AbstractOperationalStep>) -> Plan?): Plan =
			if (plan.steps.size <= 1) {
				plan
			} else {
				var work = plan

				val initialState = plan.states.first()
				plan.steps.forEach { step ->

					val candidatePlan = generator(initialState, work.steps - step)
					if (candidatePlan != null && isTargetState(candidatePlan)) {
						work = candidatePlan
					}

				}
				work
			}

	private fun tryRemoveSingles(plan: Plan): Plan = simplify(plan, this::createStrictPlan)

	private fun createStrictPlan(initial: OperationalState, steps: List<AbstractOperationalStep>): Plan? {
		var work = Plan(initial, listOf())
		steps.forEach { step ->
			val offeredSteps = stepFactory.produce(work)
			if (step in offeredSteps) {
				work = Plan.planBy(initial, work.steps + step)
			} else {
				return null
			}
		}
		return work
	}

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
		val targetPlan = silent(level = LogLevel.Off) {
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