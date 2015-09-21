package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.k0zka.finder4j.backtrack.Solution

public data class Plan(
		val state: OperationalState,
		val opSteps: List<AbstractOperationalStep>
                      ) : Solution<AbstractOperationalStep> {
	override fun getSteps(): List<AbstractOperationalStep> = opSteps
}