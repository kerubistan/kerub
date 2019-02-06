package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep

data class UnshareNfs(
		override val directory: String,
		override val host: Host
) : AbstractNfsShareStep(), InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep): Boolean =
			other is ShareNfs && other.isInverseOf(this)

	override fun updateHostConfig(config: HostConfiguration): HostConfiguration =
			config.copy(
					services = config.services.filterNot { it is NfsService && it.directory == directory }
			)
}