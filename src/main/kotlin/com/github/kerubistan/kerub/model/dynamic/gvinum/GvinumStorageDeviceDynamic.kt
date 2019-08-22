package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.utils.sumBy
import java.util.UUID

@JsonTypeName("gvinum")
data class GvinumStorageDeviceDynamic(
		override val id: UUID,
		override val items: List<GvinumStorageDeviceDynamicItem>
) : CompositeStorageDeviceDynamic<GvinumStorageDeviceDynamicItem> {
	@get:JsonIgnore
	override val freeCapacity by lazy { items.sumBy { it.freeCapacity } }
}