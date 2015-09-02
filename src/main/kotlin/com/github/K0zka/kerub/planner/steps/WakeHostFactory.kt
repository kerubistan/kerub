package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState

public object WakeHostFactory : AbstractOperationalStepFactory<WakeHost>() {
	override fun produce(state: OperationalState): List<WakeHost> {
		return listOf()
	}
}