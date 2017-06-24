package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.model.config.HostConfiguration
import org.infinispan.Cache
import java.util.UUID

class HostConfigurationDaoImpl(
		cache: Cache<UUID, HostConfiguration>,
		eventListener: EventListener)
	: HostConfigurationDao,
		IspnDaoBase<HostConfiguration, UUID>(cache, eventListener)