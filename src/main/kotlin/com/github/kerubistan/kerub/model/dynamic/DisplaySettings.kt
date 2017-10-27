package com.github.kerubistan.kerub.model.dynamic

import java.io.Serializable

data class DisplaySettings(
		val hostAddr: String,
		val port: Int,
		val password: String,
		val ca: String
) : Serializable