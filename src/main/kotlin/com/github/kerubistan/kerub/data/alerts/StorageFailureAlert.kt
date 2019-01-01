package com.github.kerubistan.kerub.data.alerts

import java.util.UUID

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