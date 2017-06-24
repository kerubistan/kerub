package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("lvm-allocation")
data class VirtualStorageLvmAllocation(
		override val hostId: UUID,
		val path: String
) : VirtualStorageBlockDeviceAllocation