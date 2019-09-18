package com.github.kerubistan.kerub.model.alerts

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("storage-failure-alert")
data class StorageFailureAlert(
		override val id: UUID,
		override val created: Long,
		override val resolved: Long?,
		override val open: Boolean,
		val hostId: UUID,
		val storageDevice: String,
		val deviceId: String?,
		val hotSwap: Boolean?
) : VirtualResourceAlert {
	init {
		this.validate()
	}
}