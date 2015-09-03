package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public interface StepExecutor<T : AbstractOperationalStep> {
	fun execute(step : T)
}