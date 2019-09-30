package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.annotations.Dynamic
import com.github.kerubistan.kerub.model.history.IgnoreDiff
import io.github.kerubistan.kroki.time.now
import org.hibernate.search.annotations.DocumentId
import java.util.UUID

@JsonTypeName("virtual-storage-dyn")
@Dynamic(VirtualStorageDevice::class)
data class VirtualStorageDeviceDynamic(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID,
		@IgnoreDiff
		override val lastUpdated: Long = now(),
		val allocations: List<VirtualStorageAllocation>
) : DynamicEntity {
	override fun updatedNow() = this.copy(lastUpdated = now())
}