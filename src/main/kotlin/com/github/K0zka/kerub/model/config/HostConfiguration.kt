package com.github.K0zka.kerub.model.config

import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.services.HostService
import java.util.UUID

data class HostConfiguration(
		override val id: UUID = UUID.randomUUID(),
		val services: List<HostService> = listOf()
) : Entity<UUID>