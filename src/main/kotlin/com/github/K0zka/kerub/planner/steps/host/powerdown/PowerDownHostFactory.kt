package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object PowerDownHostFactory : AbstractOperationalStepFactory<PowerDownHost>() {
	override fun produce(state: OperationalState): List<PowerDownHost> {
		return listOf()
	}
}