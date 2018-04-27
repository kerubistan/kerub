package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep

data class StopNfsDaemon(override val host: Host) : AbstractNfsDaemonStep(), InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is StartNfsDaemon && other.host == host

	override fun updateHostConfig(config: HostConfiguration): HostConfiguration = config.copy(
			services = config.services.filterNot { it is NfsDaemonService }
	)
}