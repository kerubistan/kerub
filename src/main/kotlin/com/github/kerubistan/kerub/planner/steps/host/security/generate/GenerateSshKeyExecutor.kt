package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh

class GenerateSshKeyExecutor(private val hostCommandExecutor: HostCommandExecutor,
							 private val hostCfgDao: HostConfigurationDao)
	: AbstractStepExecutor<GenerateSshKey, String>() {
	override fun perform(step: GenerateSshKey) = hostCommandExecutor.execute(step.host) { OpenSsh.keyGen(it) }

	override fun update(step: GenerateSshKey, updates: String) {
		hostCfgDao.updateWithDefault(
				id = step.host.id,
				defaultValue = { HostConfiguration(id = step.host.id) },
				change = { it.copy(publicKey = updates) }
		)
	}
}