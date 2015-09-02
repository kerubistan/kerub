package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState

public object PowerDownHostFactory : AbstractOperationalStepFactory<PowerDownHost>() {
	override fun produce(state: OperationalState): List<PowerDownHost> {
		return listOf()
	}
}