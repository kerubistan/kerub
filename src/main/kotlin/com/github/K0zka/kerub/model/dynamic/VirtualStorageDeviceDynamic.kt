package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.annotations.Dynamic
import org.hibernate.search.annotations.DocumentId
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("virtual-storage-dyn")
@Dynamic(VirtualStorageDevice::class)
data class VirtualStorageDeviceDynamic(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val allocation: VirtualStorageAllocation
) : DynamicEntity