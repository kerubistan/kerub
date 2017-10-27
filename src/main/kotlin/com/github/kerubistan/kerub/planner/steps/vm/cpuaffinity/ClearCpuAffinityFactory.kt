package com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object ClearCpuAffinityFactory : AbstractOperationalStepFactory<ClearCpuAffinity>() {
	override fun produce(state: OperationalState): List<ClearCpuAffinity> {
		TODO()
	}
}