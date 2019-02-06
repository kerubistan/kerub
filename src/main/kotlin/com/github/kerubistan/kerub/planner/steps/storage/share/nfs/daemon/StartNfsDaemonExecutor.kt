package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.AbstractNfsExecutor
import com.github.kerubistan.kerub.utils.junix.nfs.Exports

class StartNfsDaemonExecutor(
		private val hostManager: HostManager,
		override val hostConfigDao: HostConfigurationDao
) : AbstractNfsExecutor<StartNfsDaemon>() {
	override fun updateHostConfig(configuration: HostConfiguration, step: StartNfsDaemon): HostConfiguration =
			configuration.copy(
					services = configuration.services + NfsDaemonService()
			)

	override fun perform(step: StartNfsDaemon) {
		hostManager.getServiceManager(step.host).start(Exports)
	}
}