package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

interface StepExecutor<T : AbstractOperationalStep> {
	/**
	 * Execute the step
	 * When the method returns, the operation must be completed with success.
	 */
	fun execute(step: T)
}