package com.github.kerubistan.kerub.model.dynamic.lvm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.utils.sumBy
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("lvm")
data class LvmStorageDeviceDynamic(
		override val id: UUID,
		override val items: List<LvmStorageDeviceDynamicItem>,
		val reportedFreeCapacity : BigInteger? = null
) : CompositeStorageDeviceDynamic<LvmStorageDeviceDynamicItem> {
	@get:JsonIgnore
	override val freeCapacity by lazy { reportedFreeCapacity ?: items.sumBy { it.freeCapacity } }
}