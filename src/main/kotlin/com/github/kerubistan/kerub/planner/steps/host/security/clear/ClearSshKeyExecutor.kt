package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor

class ClearSshKeyExecutor(private val hostCommandExecutor: HostCommandExecutor,
						  private val hostCfgDao: HostConfigurationDao)
	: AbstractStepExecutor<ClearSshKey, Unit>() {

	override fun perform(step: ClearSshKey) {
		hostCommandExecutor.execute(step.host) {
			it.createSftpClient().use {
				it.remove(".ssh/id_rsa")
				it.remove(".ssh/id_rsa.pub")
			}
		}
	}

	override fun update(step: ClearSshKey, updates: Unit) {
		hostCfgDao.update(
				id = step.host.id,
				change = { it.copy(publicKey = null) }
		)
	}
}