package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.MirroredGvinumConfiguration
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("gvinum-allocation")
data class VirtualStorageGvinumAllocation(
		override val hostId: UUID,
		override val capabilityId: UUID,
		override val actualSize: BigInteger,
		val configuration: GvinumConfiguration
) : VirtualStorageBlockDeviceAllocation {

	override fun getRedundancyLevel(): Byte = when (configuration) {
		is MirroredGvinumConfiguration -> (configuration.disks.size - 1).toByte()
		else -> 0
	}

	@JsonIgnore
	override fun requires() = GvinumStorageCapability::class

	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) = "/dev/gvinum/$id"
}