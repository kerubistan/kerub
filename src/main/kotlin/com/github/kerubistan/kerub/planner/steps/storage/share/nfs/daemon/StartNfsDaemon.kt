package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep

data class StartNfsDaemon(override val host: Host) : AbstractNfsDaemonStep(), InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is StopNfsDaemon && other.host == host

	override fun updateHostConfig(config: HostConfiguration): HostConfiguration = config.copy(
			services = config.services + NfsDaemonService()
	)

}