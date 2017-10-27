package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("fs")
data class FsStorageCapability(
		override val id: UUID = UUID.randomUUID(),
		override val size: BigInteger,
		val mountPoint: String,
		val fsType: String
) : StorageCapability