package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.DocumentId
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("virtual-storage-dyn")
class VirtualStorageDeviceDynamic(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val allocation: VirtualStorageAllocation,
		val actualSize: BigInteger
) : DynamicEntity