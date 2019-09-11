package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.validateSize
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("simple")
data class SimpleStorageDeviceDynamic(override val id: UUID, override val freeCapacity: BigInteger) :
		StorageDeviceDynamic {

	override fun withFreeCapacity(freeCapacity: BigInteger) = this.copy(freeCapacity = freeCapacity)

	init {
		freeCapacity.validateSize("freeCapacity")
	}
}