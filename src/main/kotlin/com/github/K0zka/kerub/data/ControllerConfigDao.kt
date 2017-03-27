package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.controller.config.ControllerConfig

interface ControllerConfigDao {
	fun get() : ControllerConfig
	fun set(config : ControllerConfig)
}