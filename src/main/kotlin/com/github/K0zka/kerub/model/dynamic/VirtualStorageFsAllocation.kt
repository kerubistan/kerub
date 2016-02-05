package com.github.K0zka.kerub.model.dynamic

import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import java.util.UUID

data class VirtualStorageFsAllocation(
		override val hostId: UUID,
		val mountPoint: String,
		val type: VirtualDiskFormat
) : VirtualStorageAllocation