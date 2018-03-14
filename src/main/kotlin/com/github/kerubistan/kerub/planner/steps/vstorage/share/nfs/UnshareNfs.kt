package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService

data class UnshareNfs(
		override val directory: String,
		override val host: Host
) : AbstractNfsShareStep() {
	override fun updateHostConfig(config: HostConfiguration): HostConfiguration =
			config.copy(
					services = config.services.filterNot { it is NfsService && it.directory == directory }
			)
}