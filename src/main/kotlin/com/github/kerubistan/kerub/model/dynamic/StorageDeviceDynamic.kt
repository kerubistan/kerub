package com.github.kerubistan.kerub.model.dynamic

import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

data class StorageDeviceDynamic(
		val id: UUID,
		val freeCapacity: BigInteger
) : Serializable
