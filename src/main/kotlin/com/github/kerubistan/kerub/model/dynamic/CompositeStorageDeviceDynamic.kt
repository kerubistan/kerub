package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.kerubistan.kerub.utils.sumBy
import java.util.UUID

@JsonIgnoreProperties("freeCapacity")
data class CompositeStorageDeviceDynamic(override val id: UUID, val items: List<CompositeStorageDeviceDynamicItem>) : StorageDeviceDynamic {
	override val freeCapacity by lazy { items.sumBy { it.freeCapacity } }
}