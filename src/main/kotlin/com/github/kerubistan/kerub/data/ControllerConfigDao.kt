package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.controller.config.ControllerConfig

interface ControllerConfigDao {
	fun get(): ControllerConfig
	fun set(config: ControllerConfig)
}