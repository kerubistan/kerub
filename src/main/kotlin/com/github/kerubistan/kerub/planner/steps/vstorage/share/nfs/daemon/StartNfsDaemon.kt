package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertableStep

data class StartNfsDaemon(override val host: Host) : AbstractNfsDaemonStep(), InvertableStep {

	override val inverseMatcher: (AbstractOperationalStep) -> Boolean
		get() = { it is StopNfsDaemon && it.host == host }

	override fun updateHostConfig(config: HostConfiguration): HostConfiguration = config.copy(
			services = config.services + NfsDaemonService()
	)

}