package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.model.ControllerConfig
import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.services.ControllerConfigService

class ControllerConfigServiceImpl(private val configDao: ControllerConfigDao) : ControllerConfigService {
	override fun get() = configDao.get()

	override fun set(config: ControllerConfig) : ControllerConfig {
		configDao.set(config)
		return config
	}

}