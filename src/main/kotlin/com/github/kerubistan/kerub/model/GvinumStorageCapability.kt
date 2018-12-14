package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.sumBy
import java.io.Serializable
import java.util.UUID

/**
 * A gvinum storage capability of a freebsd host.
 * There can be only one such storage capability per host, each can have different performance.
 */
@JsonTypeName("gvinum")
@JsonIgnoreProperties("devicesByName", "size")
data class
GvinumStorageCapability(
		override val id: UUID = UUID.randomUUID(),
		val devices: List<GvinumStorageCapabilityDrive>,
		override val performanceInfo: Serializable? = null
) : StorageCapability {
	init {
		check(devices.isNotEmpty()) {
			"there is no gvinum capability if there are no gvinum disks"
		}
	}
	val devicesByName by lazy { devices.associateBy { it.name } }
	override val size by lazy { devices.sumBy { it.size } }
}