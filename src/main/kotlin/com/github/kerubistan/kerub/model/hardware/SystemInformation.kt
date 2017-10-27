package com.github.kerubistan.kerub.model.hardware

import java.io.Serializable
import java.util.UUID

data class SystemInformation(
		val manufacturer: String,
		val version: String,
		val family: String,
		val uuid: UUID
) : Serializable
