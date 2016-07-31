package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import java.util.UUID

@JsonTypeName("fs-allocation")
data class VirtualStorageFsAllocation(
		override val hostId: UUID,
		val mountPoint: String,
		val type: VirtualDiskFormat
) : VirtualStorageAllocation