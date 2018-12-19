package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.kerubistan.kerub.utils.sumBy
import java.util.UUID

data class CompositeStorageDeviceDynamic(override val id: UUID, val items: List<CompositeStorageDeviceDynamicItem>) : StorageDeviceDynamic {
	@delegate:JsonIgnore
	@delegate:Transient
	override val freeCapacity by lazy { items.sumBy { it.freeCapacity } }
}