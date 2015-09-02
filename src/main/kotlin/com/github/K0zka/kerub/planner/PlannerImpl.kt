package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.messages.EntityMessage
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.CompositeStepFactory
import com.github.K0zka.kerub.utils.getLogger
import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy

public class PlannerImpl(
		private val backtrack: BacktrackService,
		private val executor: PlanExecutor,
		private val builder: OperationalStateBuilder
                        ) : Planner {
	companion object {
		val logger = getLogger(PlannerImpl::class)
	}

	override fun onEvent(msg: EntityMessage) {
		val strategy = FirstSolutionTerminationStrategy<OperationalStateTransformation, AbstractOperationalStep>()

		backtrack.backtrack(
				OperationalStateTransformation(
						state = builder.buildState()
				                              ),
				CompositeStepFactory,
				strategy,
				strategy
		                   )
		val solution = strategy.getSolution();

	}
}