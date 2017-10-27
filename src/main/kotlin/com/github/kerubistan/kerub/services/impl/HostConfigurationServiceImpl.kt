package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.services.HostConfigurationService
import sun.security.util.Cache
import java.util.UUID

class HostConfigurationServiceImpl(private val cache: Cache<UUID, HostConfiguration>) : HostConfigurationService {
	override fun getById(id: UUID): HostConfiguration =
			cache[id]
}