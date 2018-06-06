package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object GenerateSshKeyFactory : AbstractOperationalStepFactory<GenerateSshKey>() {
	override fun produce(state: OperationalState): List<GenerateSshKey> = state.hosts.values
			.filter { it.dynamic?.status == HostStatus.Up && it.config?.publicKey == null }
			.map { GenerateSshKey(host = it.stat) }
}