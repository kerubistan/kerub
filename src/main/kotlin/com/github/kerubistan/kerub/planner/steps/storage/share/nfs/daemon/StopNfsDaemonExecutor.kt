package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.AbstractNfsExecutor
import com.github.kerubistan.kerub.utils.junix.nfs.Exports

class StopNfsDaemonExecutor(
		private val hostManager: HostManager,
		override val hostConfigDao: HostConfigurationDao
) : AbstractNfsExecutor<StopNfsDaemon>() {
	override fun updateHostConfig(configuration: HostConfiguration, step: StopNfsDaemon): HostConfiguration =
			configuration.copy(
					services = configuration.services - NfsDaemonService()
			)

	override fun perform(step: StopNfsDaemon) {
		hostManager.getServiceManager(step.host).stop(Exports)
	}

}