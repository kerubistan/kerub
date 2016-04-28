package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep

abstract class AbstractKsmExecutor<T : HostStep>(private val exec: HostCommandExecutor,
												 private val hostDynDao: HostDynamicDao,
												 private val enabled: Boolean) : AbstractStepExecutor<T, Unit>() {

	final override fun perform(step: T) {
		controlKsm(step.host, exec, enabled)
	}

	final override fun update(step: T, updates : Unit) {
		hostDynDao.update(step.host.id, {
			host ->
			host.copy(
					ksmEnabled = enabled
			)
		})
	}
}