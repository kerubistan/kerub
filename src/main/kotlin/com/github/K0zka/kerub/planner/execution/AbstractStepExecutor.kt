package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

abstract class AbstractStepExecutor<T: AbstractOperationalStep> : StepExecutor<T> {

	abstract fun prepare(step : T)
	abstract fun perform(step : T)
	abstract fun verify(step : T)
	abstract fun update(step: T)

	override fun execute(step: T) {
		prepare(step)
		perform(step)
		verify(step)
		update(step)
	}
}