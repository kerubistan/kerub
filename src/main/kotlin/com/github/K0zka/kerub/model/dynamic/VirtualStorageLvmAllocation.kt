package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("lvm-allocation")
data class VirtualStorageLvmAllocation(
		override val hostId: UUID,
		override val actualSize: BigInteger,
		val path: String
) : VirtualStorageBlockDeviceAllocation {
	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) = path
}