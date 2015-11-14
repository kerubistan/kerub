package com.github.K0zka.kerub.model.dynamic

import com.github.K0zka.kerub.model.VirtualMachineStatus
import java.math.BigInteger
import java.util.UUID

public data class VirtualMachineDynamic(
		override
		val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val hostId: UUID,
		val status: VirtualMachineStatus = VirtualMachineStatus.Down,
		val memoryUsed: BigInteger
) : DynamicEntity
