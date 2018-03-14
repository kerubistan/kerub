package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService

data class StartNfsDaemon(override val host: Host) : AbstractNfsDaemonStep() {
	override fun updateHostConfig(config: HostConfiguration): HostConfiguration = config.copy(
			services = config.services + NfsDaemonService()
	)
}