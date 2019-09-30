package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.history.IgnoreDiff
import io.github.kerubistan.kroki.time.now
import java.util.UUID

@JsonTypeName("virtual-network-dynamic")
data class VirtualNetworkDynamic(
		@IgnoreDiff
		override val lastUpdated: Long,
		override val id: UUID
) : DynamicEntity {
	override fun updatedNow() = this.copy(lastUpdated = now())
}