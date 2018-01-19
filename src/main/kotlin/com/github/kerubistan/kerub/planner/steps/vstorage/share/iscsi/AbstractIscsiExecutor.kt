package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.genPassword

abstract class AbstractIscsiExecutor<T : AbstractIscsiOperation>(
		private val hostConfigDao: HostConfigurationDao,
		protected val hostExecutor: HostCommandExecutor,
		protected val hostManager: HostManager) : AbstractStepExecutor<T, String>() {

	override fun perform(step: T): String {
		val password = genPassword()
		perform(step, password)
		return password
	}

	abstract fun perform(step: T, password: String)

	override fun update(step: T, updates: String) {
		val config = hostConfigDao[step.host.id] ?: HostConfiguration(id = step.host.id)
		config.copy()
		hostConfigDao.update(
				config.copy(
						services = config.services + IscsiService(
								vstorageId = step.vstorage.id,
								password = updates
						)
				)
		)
	}

}