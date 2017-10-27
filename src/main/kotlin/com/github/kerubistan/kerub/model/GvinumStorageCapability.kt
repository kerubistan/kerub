package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("gvinum")
data class GvinumStorageCapability(
		override val id: UUID = UUID.randomUUID(),
		override val size: BigInteger,
		val name: String,
		val device: String
) : StorageCapability