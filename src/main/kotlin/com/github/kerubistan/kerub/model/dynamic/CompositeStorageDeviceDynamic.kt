package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.kerubistan.kroki.numbers.sumBy
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("composite")
data class CompositeStorageDeviceDynamic(
		override val id: UUID,
		val reportedFreeCapacity: BigInteger? = null,
		val items: List<CompositeStorageDeviceDynamicItem> = listOf(),
		val pools: List<StoragePoolDynamic> = listOf()
) : StorageDeviceDynamic {

	override val freeCapacity: BigInteger = reportedFreeCapacity
			?: items.sumBy(CompositeStorageDeviceDynamicItem::freeCapacity)

	override fun withFreeCapacity(freeCapacity: BigInteger) = this.copy(
			reportedFreeCapacity = freeCapacity
	)
}