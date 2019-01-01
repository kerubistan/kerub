package com.github.kerubistan.kerub.data.alerts

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.now
import java.util.UUID

@JsonTypeName("data-loss-alert")
data class DataLossAlert(
		override val id: UUID = UUID.randomUUID(),
		override val created: Long = now(),
		override val resolved: Long?,
		override val open: Boolean = true,
		val storageId : UUID
) : VirtualResourceAlert {
	init {
		this.validate()
	}
}