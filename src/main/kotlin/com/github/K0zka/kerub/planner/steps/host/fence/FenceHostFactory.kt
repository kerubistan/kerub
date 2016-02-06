package com.github.K0zka.kerub.planner.steps.host.fence

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object FenceHostFactory : AbstractOperationalStepFactory<FenceHost>() {
	override fun produce(state: OperationalState): List<FenceHost> {
		throw UnsupportedOperationException()
	}

}