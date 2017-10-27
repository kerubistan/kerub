package com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object SetCpuAffinityFactory : AbstractOperationalStepFactory<SetCpuAffinity>() {
	override fun produce(state: OperationalState): List<SetCpuAffinity> = TODO()

}