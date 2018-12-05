package com.github.kerubistan.kerub.model.dynamic

import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

interface StorageDeviceDynamic : Serializable {
	val id: UUID
	val freeCapacity: BigInteger
}

