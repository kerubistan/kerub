package com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor

class SetCpuAffinityExecutor(private val hostExecutor: HostCommandExecutor) : AbstractStepExecutor<SetCpuAffinity, Unit>() {
	override fun perform(step: SetCpuAffinity) {
		hostExecutor.execute(host = step.host) {
			TODO()
		}
	}

	override fun update(step: SetCpuAffinity, updates: Unit) {
		TODO()
	}
}