package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object ClearCpuAffinityFactory : AbstractOperationalStepFactory<ClearCpuAffinity>() {
	override fun produce(state: OperationalState): List<ClearCpuAffinity> {
		TODO()
	}
}