package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi

import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.config.HostConfiguration
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.genPassword

abstract class AbstractIscsiExecutor<T : AbstractIscsiShare>(
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