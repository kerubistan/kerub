package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh

class RemovePublicKeyExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostCfgDao: HostConfigurationDao
) : AbstractStepExecutor<RemovePublicKey, Unit>() {
	override fun perform(step: RemovePublicKey) {
		hostCommandExecutor.execute(step.host) {
			OpenSsh.unauthorize(it, step.publicKey)
		}
		hostCommandExecutor.execute(step.hostOfKey) {
			OpenSsh.removeKnownHost(it, step.host.address)
		}
	}

	override fun update(step: RemovePublicKey, updates: Unit) {
		hostCfgDao.update(step.host.id) {
			it.copy(
					acceptedPublicKeys = it.acceptedPublicKeys - step.publicKey
			)
		}
	}
}