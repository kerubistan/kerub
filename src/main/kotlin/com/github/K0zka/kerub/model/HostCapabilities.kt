package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.Field
import com.github.K0zka.kerub.os.OperatingSystem
import com.github.K0zka.kerub.utils.SoftwarePackage
import java.io.Serializable

public data class HostCapabilities (
		Field
		val os: OperatingSystem?,
		Field
		val distribution: SoftwarePackage?,
		Field
		val installedSoftware: List<SoftwarePackage> = serializableListOf(),
		Field
		val devices: List<PciDevice> = serializableListOf(),
		Field
		val cpuArchitecture: String,
		Field
		val totalMemory: Long
                                   )
: Serializable