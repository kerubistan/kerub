package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.utils.sumBy
import java.math.BigInteger
import java.util.UUID

data class CompositeStorageDeviceDynamic(
		override val id: UUID,
		val reportedFreeCapacity: BigInteger? = null,
		val items: List<CompositeStorageDeviceDynamicItem> = listOf()
) : StorageDeviceDynamic {

	override val freeCapacity: BigInteger = reportedFreeCapacity ?: items.sumBy { it.freeCapacity }

	override fun withFreeCapacity(freeCapacity: BigInteger) = this.copy(
			reportedFreeCapacity = freeCapacity
	)


}