package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger
import java.util.UUID

data class SimpleStorageDeviceDynamic(override val id: UUID, override val freeCapacity: BigInteger) :
		StorageDeviceDynamic {
	init {
		freeCapacity.validateSize("freeCapacity")
	}
}