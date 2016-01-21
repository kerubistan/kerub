package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

/**
 * Generic framework class to better organize what a step executor have to do.
 */
abstract class AbstractStepExecutor<T: AbstractOperationalStep> : StepExecutor<T> {

	/**
	 * Check if the pre-conditions of performing the action are met, throw an exception if not.
	 * Default implementation does nothing, since most commands can be executed without pre-check.
	 */
	open fun prepare(step : T) {

	}

	/**
	 * Perform the step.
	 */
	abstract fun perform(step : T)

	/**
	 * Verify the execution of the step.
	 * By default does nothing, since most junix commands should fail on error, specific tools can use this if not.
	 */
	open fun verify(step : T) {

	}

	/**
	 * Update the database with the new fact
	 */
	abstract fun update(step: T)

	final override fun execute(step: T) {
		prepare(step)
		perform(step)
		verify(step)
		update(step)
	}
}