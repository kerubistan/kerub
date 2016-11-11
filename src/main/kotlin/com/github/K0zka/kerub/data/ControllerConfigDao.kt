package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.ControllerConfig

interface ControllerConfigDao {
	fun get() : ControllerConfig
	fun set(config : ControllerConfig)
}