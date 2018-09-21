package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("lvm")
data class LvmStorageCapability(
		override val id: UUID = UUID.randomUUID(),
		override val size: BigInteger,
		val volumeGroupName: String,
		val physicalVolumes: List<BigInteger>,
		override val performanceInfo: Serializable? = null
) : StorageCapability