package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.dynamic.gvinum.GvinumConfiguration
import java.util.UUID

@JsonTypeName("gvinum-allocation")
data class VirtualStorageGvinumAllocation(
		override val hostId: UUID,
		val configuration: GvinumConfiguration
) : VirtualStorageBlockDeviceAllocation {
	override fun getPath(id: UUID) = "/dev/gvinum/${id}"
}