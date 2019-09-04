package com.github.kerubistan.kerub.model.dynamic

import io.github.kerubistan.kroki.time.now
import java.util.UUID

data class VirtualNetworkDynamic(
		override val lastUpdated: Long,
		override val id: UUID
) : DynamicEntity {
	override fun updatedNow() = this.copy(lastUpdated = now())
}