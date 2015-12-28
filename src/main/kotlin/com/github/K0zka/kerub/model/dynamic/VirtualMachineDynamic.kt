package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.K0zka.kerub.model.VirtualMachineStatus
import org.hibernate.search.annotations.DocumentId
import java.math.BigInteger
import java.util.UUID

public data class VirtualMachineDynamic(
		@DocumentId
		@JsonProperty("id")
		override
		val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val hostId: UUID,
		val status: VirtualMachineStatus = VirtualMachineStatus.Down,
		val memoryUsed: BigInteger,
		val cpuUsage: CpuStat = CpuStat.zero
) : DynamicEntity
