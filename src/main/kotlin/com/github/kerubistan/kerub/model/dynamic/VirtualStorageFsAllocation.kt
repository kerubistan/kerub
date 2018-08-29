package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("fs-allocation")
data class VirtualStorageFsAllocation(
		override val hostId: UUID,
		override val actualSize: BigInteger,
		val mountPoint: String,
		val type: VirtualDiskFormat,
		val fileName : String,
		val backingFile : String? = null
) : VirtualStorageAllocation {
	override fun resize(newSize: BigInteger): VirtualStorageAllocation = this.copy(actualSize = newSize)

	override fun getPath(id: UUID) =
		"$mountPoint/$id"
}