package com.github.kerubistan.kerub.model.alerts

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("host-overheat-alert")
data class HostOverheatingAlert(
		override val id: UUID,
		override val created: Long,
		override val resolved: Long?,
		override val open: Boolean,
		val hostId: UUID
) : InfrastructureAlert {
	init {
		this.validate()
	}
}