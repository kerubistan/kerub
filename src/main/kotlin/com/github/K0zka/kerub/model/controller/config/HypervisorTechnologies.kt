package com.github.K0zka.kerub.model.controller.config

import java.io.Serializable

data class HypervisorTechnologies(
		val kvmEnabled: Boolean = true,
		val xenEnabled: Boolean = false,
		val bhyveEnabled: Boolean = false,
		val virtualBoxEnabled: Boolean = false
) : Serializable