package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("simple")
data class SimpleStorageDeviceDynamic(override val id: UUID, override val freeCapacity: BigInteger) :
		StorageDeviceDynamic {
	init {
		freeCapacity.validateSize("freeCapacity")
	}
}