package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join

object ClearSshKeyFactory : AbstractOperationalStepFactory<ClearSshKey>() {
	override fun produce(state: OperationalState): List<ClearSshKey> {

		val allInstalledSshKeys = state.hosts.values.map { it.config?.acceptedPublicKeys }.filterNotNull().join().toSet()

		return state.hosts.values.filter { it.config?.publicKey != null && !(it.config.publicKey in allInstalledSshKeys) }
				.map { ClearSshKey(it.stat) }

	}
}