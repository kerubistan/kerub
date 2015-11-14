package com.github.K0zka.kerub.model.dynamic

import java.math.BigInteger
import java.util.UUID

class VirtualStorageDeviceDynamic(
		override val id: UUID,
		override val lastUpdated: Long = System.currentTimeMillis(),
		val hostId : UUID,
		val actualSize : BigInteger
) : DynamicEntity