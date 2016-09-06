package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.model.config.HostConfiguration
import com.github.K0zka.kerub.services.HostConfigurationService
import sun.security.util.Cache
import java.util.UUID

class HostConfigurationServiceImpl(private val cache: Cache<UUID, HostConfiguration>) : HostConfigurationService {
	override fun getById(id: UUID): HostConfiguration =
			cache[id]
}