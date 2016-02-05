package com.github.K0zka.kerub.planner.steps.vm.base

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor

abstract class HypervisorStepExcecutor<T : HostStep>(private val hostManager: HostManager)
: AbstractStepExecutor<T>() {

	override fun perform(step: T) {
		execute(requireNotNull(hostManager.getHypervisor(step.host), { "No hpervisor found on host ${step.host}" }), step)
	}

	abstract fun execute(hypervisor: Hypervisor, step: T)
}