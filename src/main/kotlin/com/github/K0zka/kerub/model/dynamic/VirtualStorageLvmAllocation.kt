package com.github.K0zka.kerub.model.dynamic

import java.util.UUID

data class VirtualStorageLvmAllocation(
		override val hostId: UUID,
		val volGroup: String
) : VirtualStorageAllocation