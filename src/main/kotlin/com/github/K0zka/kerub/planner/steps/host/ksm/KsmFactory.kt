package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object KsmFactory : AbstractOperationalStepFactory<AbstractOperationalStep>() {
	override fun produce(state: OperationalState): List<AbstractOperationalStep> {
		return listOf()
	}
}