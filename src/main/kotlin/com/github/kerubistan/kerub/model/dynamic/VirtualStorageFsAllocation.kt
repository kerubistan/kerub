package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("fs-allocation")
data class VirtualStorageFsAllocation(
		override val hostId: UUID,
		override val capabilityId: UUID,
		override val actualSize: BigInteger,
		val mountPoint: String,
		override val type: VirtualDiskFormat,
		val fileName: String,
		val backingFile: String? = null
) : VirtualStorageAllocation {

	init {
		actualSize.validateSize("actualSize")
	}

	override fun getRedundancyLevel(): Byte = 0

	@JsonIgnore
	override fun requires() = FsStorageCapability::class

	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) =
			"$mountPoint/$id"
}