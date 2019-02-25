package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.utils.validateSize
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("lvm")
data class LvmStorageCapability(
		override val id: UUID = UUID.randomUUID(),
		override val size: BigInteger,
		val volumeGroupName: String,
		val physicalVolumes: Map<String, BigInteger>,
		override val performanceInfo: Serializable? = null
) : VolumeManagerStorageCapability {

	override val storageDevices by lazy { physicalVolumes.map { it.key } }

	init {
		size.validateSize("size")
		check(physicalVolumes.isNotEmpty()) {
			"There should be at least one PV in a VG"
		}
	}
}