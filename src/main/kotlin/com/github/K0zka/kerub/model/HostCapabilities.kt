package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.Field
import com.github.K0zka.kerub.os.OperatingSystem
import com.github.K0zka.kerub.utils.SoftwarePackage
import java.io.Serializable
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.hardware.ChassisInformation

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
		val totalMemory: Long,
        Field
		val system: SystemInformation? = null,
        Field
        val cpus: List<ProcessorInformation> = listOf(),
        Field
        val chassis : ChassisInformation? = null

        )
: Serializable