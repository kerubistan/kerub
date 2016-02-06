package com.github.K0zka.kerub.planner.steps.host.install

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object InstallSoftwareFactory : AbstractOperationalStepFactory<InstallSoftware>() {
	override fun produce(state: OperationalState): List<InstallSoftware> {
		throw UnsupportedOperationException()
	}
}