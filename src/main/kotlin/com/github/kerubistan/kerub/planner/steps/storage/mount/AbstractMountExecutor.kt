package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import org.apache.sshd.client.session.ClientSession

abstract class AbstractMountExecutor<T : AbstractNfsMount> : AbstractStepExecutor<T, Unit>() {

	protected abstract val hostCommandExecutor: HostCommandExecutor
	protected abstract val hostConfigurationDao: HostConfigurationDao

	final override fun perform(step: T) {
		hostCommandExecutor.execute(step.host) {
			performOnHost(it, step)
		}
	}

	abstract fun performOnHost(session: ClientSession,
							   step: T)

	final override fun update(step: T, updates: Unit) {
		hostConfigurationDao.update(step.host.id,
									retrieve = { hostConfigurationDao[step.host.id] ?: HostConfiguration() },
									change = { updateHostConfiguration(it, step) })
	}

	abstract fun updateHostConfiguration(
			hostConfiguration: HostConfiguration, step: T): HostConfiguration

}