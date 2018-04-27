package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep

data class ShareNfs(
		override val directory: String,
		override val host: Host
) : AbstractNfsShareStep(), InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) =
			other is UnshareNfs && other.directory == directory && other.host == host

	override fun updateHostConfig(config: HostConfiguration): HostConfiguration = config.copy(
			services = config.services + NfsService(directory)
	)

}