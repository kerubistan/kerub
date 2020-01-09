package com.github.kerubistan.kerub.model.alerts

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("host-lost-alert")
data class HostLostAlert(
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