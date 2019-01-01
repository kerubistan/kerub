package com.github.kerubistan.kerub.data.alerts

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("data-loss-alert")
data class NetworkLinkDownAlert(
		override val id: UUID,
		override val created: Long,
		override val resolved: Long?,
		override val open: Boolean,
		val hostId : UUID
) : InfrastructureAlert {
	init {
		this.validate()
	}
}