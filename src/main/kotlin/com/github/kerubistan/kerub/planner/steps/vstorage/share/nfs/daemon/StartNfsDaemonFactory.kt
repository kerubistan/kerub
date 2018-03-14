package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.utils.junix.nfs.Exports

object StartNfsDaemonFactory : AbstractOperationalStepFactory<StartNfsDaemon>() {
	override fun produce(state: OperationalState): List<StartNfsDaemon> =
			factoryFeature(state.controllerConfig.storageTechnologies.nfsEnabled) {
				state.hosts.values.filter { Exports.available(it.stat.capabilities) }
						.filterNot { it.config?.services?.any { it is NfsDaemonService } ?: false }
						.map { StartNfsDaemon(it.stat) }
			}
}