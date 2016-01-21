package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.search.annotations.DocumentId
import java.math.BigInteger
import java.util.UUID

class VirtualStorageDeviceDynamic(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val allocation : VirtualStorageAllocation,
		val actualSize : BigInteger
) : DynamicEntity