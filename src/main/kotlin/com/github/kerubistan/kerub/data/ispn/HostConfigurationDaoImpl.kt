package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.model.config.HostConfiguration
import org.infinispan.Cache
import java.util.UUID

class HostConfigurationDaoImpl(
		cache: Cache<UUID, HostConfiguration>,
		eventListener: EventListener)
	: HostConfigurationDao,
		IspnDaoBase<HostConfiguration, UUID>(cache, eventListener)