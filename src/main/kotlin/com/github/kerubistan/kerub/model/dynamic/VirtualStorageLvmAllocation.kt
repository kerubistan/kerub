package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.LvmStorageCapability
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.util.UUID

@JsonTypeName("lvm-allocation")
data class VirtualStorageLvmAllocation(
		override val hostId: UUID,
		override val capabilityId: UUID,
		override val actualSize: BigInteger,
		val path: String,
		val pool: String? = null,
		val vgName : String,
		val mirrors : Byte = 0
) : VirtualStorageBlockDeviceAllocation {

	override fun getRedundancyLevel(): Byte = mirrors

	init {
		check(actualSize >= ZERO) {
			"Actual size ($actualSize) must not be negative"
		}
		check(mirrors >= 0) {
			"number of mirrors must be at least 0"
		}
	}

	@JsonIgnore
	override fun requires() = LvmStorageCapability::class

	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) = path
}