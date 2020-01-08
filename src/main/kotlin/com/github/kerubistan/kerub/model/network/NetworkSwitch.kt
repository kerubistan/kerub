package com.github.kerubistan.kerub.model.network

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import java.util.UUID
import java.util.UUID.randomUUID

@JsonTypeName("network-switch")
data class NetworkSwitch(
		override
		val id: UUID = randomUUID(),
		val type: String?,
		val location: String?,
		val switchingCapacityPerSec: Long?,
		// for each different port speeds the number of ports
		// like e.g. 10 GB/s -> 2, 1 GB/s -> 16
		val portSpeeds: Map<Long, Int>?
) : Entity<UUID>