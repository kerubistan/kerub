package com.github.kerubistan.kerub.model.dynamic

import java.util.UUID

data class VirtualNetworkDynamic(
		override val lastUpdated: Long,
		override val id: UUID
) : DynamicEntity