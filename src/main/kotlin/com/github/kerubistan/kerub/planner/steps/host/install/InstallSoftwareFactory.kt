package com.github.kerubistan.kerub.planner.steps.host.install

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object InstallSoftwareFactory : AbstractOperationalStepFactory<InstallSoftware>() {
	override fun produce(state: OperationalState): List<InstallSoftware> {
		throw UnsupportedOperationException()
	}
}