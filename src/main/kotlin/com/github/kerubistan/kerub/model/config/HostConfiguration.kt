package com.github.kerubistan.kerub.model.config

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.services.HostService
import java.util.UUID

data class HostConfiguration(
		override val id: UUID = UUID.randomUUID(),
		val services: List<HostService> = listOf()
) : Entity<UUID>