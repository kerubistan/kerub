package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.LvmStorageCapability
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("lvm-allocation")
data class VirtualStorageLvmAllocation(
		override val hostId: UUID,
		override val capabilityId: UUID,
		override val actualSize: BigInteger,
		val path: String,
		val pool: String? = null,
		val vgName : String
) : VirtualStorageBlockDeviceAllocation {

	@JsonIgnore
	override val requires = LvmStorageCapability::class

	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) = path
}