package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.annotations.Dynamic
import com.github.kerubistan.kerub.utils.validateSize
import io.github.kerubistan.kroki.time.now
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
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
		override val lastUpdated: Long = now(),
		@Field
		val status: HostStatus = HostStatus.Up,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val userCpu: Byte? = null,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val systemCpu: Byte? = null,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val idleCpu: Byte? = null,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val memFree: BigInteger? = null,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val memUsed: BigInteger? = null,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val memSwapped: BigInteger? = null,
		val ksmEnabled: Boolean = false,
		val cpuStats: List<CpuStat> = listOf(),
		val storageStatus: List<StorageDeviceDynamic> = listOf(),
		val storageDeviceHealth: Map<String, Boolean> = mapOf(),
		val cpuTemperature: List<Int> = listOf()
) : DynamicEntity {

	init {
		memFree?.validateSize("memFree")
		memSwapped?.validateSize("memSwapped")
		memUsed?.validateSize("memUsed")
	}

	@get:JsonIgnore
	val storageStatusById by lazy {
		storageStatus.associateBy { it.id }
	}
}
