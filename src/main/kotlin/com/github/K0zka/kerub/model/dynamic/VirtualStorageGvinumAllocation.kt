package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("gvinum-allocation")
data class VirtualStorageGvinumAllocation(
		override val hostId: UUID,
		val configuration : GvinumConfiguration
) : VirtualStorageBlockDeviceAllocation