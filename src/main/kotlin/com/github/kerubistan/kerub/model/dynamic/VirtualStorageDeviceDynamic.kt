package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.annotations.Dynamic
import com.github.kerubistan.kerub.utils.now
import org.hibernate.search.annotations.DocumentId
import java.util.UUID

@JsonTypeName("virtual-storage-dyn")
@Dynamic(VirtualStorageDevice::class)
data class VirtualStorageDeviceDynamic(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID,
		override val lastUpdated: Long = now(),
		val allocations: List<VirtualStorageAllocation>
) : DynamicEntity {
	@Deprecated("Use allocations", replaceWith = ReplaceWith(expression = "allocations"))
	val allocation : VirtualStorageAllocation
		@JsonIgnore
		get() = allocations.first()
}