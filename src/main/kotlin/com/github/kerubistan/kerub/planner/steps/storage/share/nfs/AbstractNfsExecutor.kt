package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor

abstract class AbstractNfsExecutor<T : AbstractNfsStep> : AbstractStepExecutor<T, Unit>() {
	abstract val hostConfigDao: HostConfigurationDao
	final override fun update(step: T, updates: Unit) {
		hostConfigDao.update(step.host.id, retrieve = {
			hostConfigDao.get(id = step.host.id) ?: HostConfiguration(id = step.host.id)
		}) {
			updateHostConfig(it, step)
		}
	}

	protected abstract fun updateHostConfig(configuration: HostConfiguration,
											step: T): HostConfiguration

}