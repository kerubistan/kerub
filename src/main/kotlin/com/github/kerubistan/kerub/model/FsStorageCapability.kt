package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.validateSize
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("fs")
data class FsStorageCapability(
		override val id: UUID = UUID.randomUUID(),
		override val size: BigInteger,
		val mountPoint: String,
		val fsType: String,
		override val performanceInfo: Serializable? = null
) : StorageCapability {
	init {
		size.validateSize("size")
	}
}