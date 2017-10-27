package com.github.kerubistan.kerub.planner.steps.host.fence

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object FenceHostFactory : AbstractOperationalStepFactory<FenceHost>() {
	override fun produce(state: OperationalState): List<FenceHost> {
		throw UnsupportedOperationException()
	}

}