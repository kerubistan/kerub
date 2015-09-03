package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object WakeHostFactory : AbstractOperationalStepFactory<WakeHost>() {
	override fun produce(state: OperationalState): List<WakeHost> {
		return listOf()
	}
}