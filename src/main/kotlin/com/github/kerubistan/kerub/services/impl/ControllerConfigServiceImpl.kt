package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.services.ControllerConfigService

class ControllerConfigServiceImpl(private val configDao: ControllerConfigDao) : ControllerConfigService {
	override fun get() = configDao.get()

	override fun set(config: ControllerConfig): ControllerConfig {
		configDao.set(config)
		return config
	}

}