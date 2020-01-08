package com.github.kerubistan.kerub.model.network

import java.io.Serializable
import java.util.UUID

data class EthernetPort(
		val device: String,
		val portSpeed: Long,
		/**
		 * The ID of the NetworkSwitch the port is physically connected to
		 */
		val switchId: UUID?
) : Serializable