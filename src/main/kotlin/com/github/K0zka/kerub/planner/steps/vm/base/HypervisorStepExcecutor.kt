package com.github.K0zka.kerub.planner.steps.vm.base

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor

abstract class HypervisorStepExcecutor<T : HostStep, U>(protected val hostManager: HostManager)
: AbstractStepExecutor<T, U>() {

	override fun perform(step: T) =
		execute(getHypervisor(step), step)

	protected fun getHypervisor(step: T) = requireNotNull(hostManager.getHypervisor(step.host),
			{ "No hypervisor found on host ${step.host}" })

	abstract fun execute(hypervisor: Hypervisor, step: T) : U
}