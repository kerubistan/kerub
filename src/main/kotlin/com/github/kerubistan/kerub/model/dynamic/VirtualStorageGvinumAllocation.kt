package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumConfiguration
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("gvinum-allocation")
data class VirtualStorageGvinumAllocation(
		override val hostId: UUID,
		override val actualSize: BigInteger,
		val configuration: GvinumConfiguration
) : VirtualStorageBlockDeviceAllocation {
	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) = "/dev/gvinum/${id}"
}