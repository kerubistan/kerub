package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertableStep

data class UnshareNfs(
		override val directory: String,
		override val host: Host
) : AbstractNfsShareStep(), InvertableStep {

	override val inverseMatcher: (AbstractOperationalStep) -> Boolean
		get() = { it is ShareNfs && it.directory == directory && it.host == host }

	override fun updateHostConfig(config: HostConfiguration): HostConfiguration =
			config.copy(
					services = config.services.filterNot { it is NfsService && it.directory == directory }
			)
}