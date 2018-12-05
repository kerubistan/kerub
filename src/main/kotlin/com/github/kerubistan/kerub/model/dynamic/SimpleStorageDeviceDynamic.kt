package com.github.kerubistan.kerub.model.dynamic

import java.math.BigInteger
import java.util.UUID

data class SimpleStorageDeviceDynamic(override val id: UUID, override val freeCapacity: BigInteger) : StorageDeviceDynamic