package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState

public object CompositeStepFactory : AbstractOperationalStepFactory<AbstractOperationalStep>() {
	override fun produce(state: OperationalState): List<AbstractOperationalStep> {
		throw UnsupportedOperationException()
	}
}