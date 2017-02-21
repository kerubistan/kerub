package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor

class ClearCpuAffinityExecutor(private val hostExecutor: HostCommandExecutor) : AbstractStepExecutor<ClearCpuAffinity, Unit>() {
	override fun perform(step: ClearCpuAffinity) {
		TODO()
	}

	override fun update(step: ClearCpuAffinity, updates: Unit) {
		TODO()
	}
}