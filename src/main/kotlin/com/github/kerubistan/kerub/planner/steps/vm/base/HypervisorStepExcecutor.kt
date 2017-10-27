package com.github.kerubistan.kerub.planner.steps.vm.base

import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor

abstract class HypervisorStepExcecutor<T : HostStep, U>(protected val hostManager: HostManager)
	: AbstractStepExecutor<T, U>() {

	override fun perform(step: T) =
			execute(getHypervisor(step), step)

	protected fun getHypervisor(step: T) = requireNotNull(hostManager.getHypervisor(step.host),
			{ "No hypervisor found on host ${step.host}" })

	abstract fun execute(hypervisor: Hypervisor, step: T): U
}