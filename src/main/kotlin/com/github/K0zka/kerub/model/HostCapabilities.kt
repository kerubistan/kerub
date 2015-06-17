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
		JsonView(Simple::class)
		Field
		val os: OperatingSystem?,
		JsonView(Simple::class)
		Field
		val distribution: SoftwarePackage?,
		JsonView(Full::class)
		Field
		val installedSoftware: List<SoftwarePackage> = listOf(),
		JsonView(Detailed::class)
		Field
		val devices: List<PciDevice> = listOf(),
		JsonView(Simple::class)
		Field
		val cpuArchitecture: String,
		JsonView(Simple::class)
		Field
		val totalMemory: Long,
		JsonView(Detailed::class)
		Field
		val system: SystemInformation? = null,
        Field
        JsonView(Detailed::class)
        val cpus: List<ProcessorInformation> = listOf(),
        JsonView(Detailed::class)
        Field
        val chassis : ChassisInformation? = null

        )
: Serializable