package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonView
import org.hibernate.search.annotations.Field
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.utils.SoftwarePackage
import java.io.Serializable
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.model.views.Full
import com.github.K0zka.kerub.model.views.Simple

public data class HostCapabilities (
		JsonView(javaClass<Simple>())
		Field
		val os: OperatingSystem?,
		JsonView(javaClass<Simple>())
		Field
		val distribution: SoftwarePackage?,
		JsonView(javaClass<Full>())
		Field
		val installedSoftware: List<SoftwarePackage> = serializableListOf(),
		JsonView(javaClass<Detailed>())
		Field
		val devices: List<PciDevice> = serializableListOf(),
		JsonView(javaClass<Simple>())
		Field
		val cpuArchitecture: String,
		JsonView(javaClass<Simple>())
		Field
		val totalMemory: Long,
		JsonView(javaClass<Detailed>())
		Field
		val system: SystemInformation? = null,
        Field
        JsonView(javaClass<Detailed>())
        val cpus: List<ProcessorInformation> = listOf(),
        JsonView(javaClass<Detailed>())
        Field
        val chassis : ChassisInformation? = null

        )
: Serializable