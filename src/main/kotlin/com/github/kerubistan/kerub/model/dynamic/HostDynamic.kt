package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.annotations.Dynamic
import org.hibernate.search.annotations.DocumentId
import java.math.BigInteger
import java.util.UUID

/**
 * Dynamic general information about the status of a host.
 */
@JsonTypeName("host-dyn")
@Dynamic(Host::class)
data class HostDynamic(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val status: HostStatus = HostStatus.Up,
		val userCpu: Byte? = null,
		val systemCpu: Byte? = null,
		val idleCpu: Byte? = null,
		val memFree: BigInteger? = null,
		val memUsed: BigInteger? = null,
		val memSwapped: BigInteger? = null,
		val ksmEnabled: Boolean = false,
		val cpuStats: List<CpuStat> = listOf(),
		val storageStatus: List<StorageDeviceDynamic> = listOf()
)
	: DynamicEntity
