package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.Field
import com.github.K0zka.kerub.os.OperatingSystem
import com.github.K0zka.kerub.utils.SoftwarePackage

public class HostCapabilities(
		Field
		val os: OperatingSystem?,
		Field
		val distribution: SoftwarePackage?,
		Field
		val installedSoftware: List<SoftwarePackage> = listOf(),
		Field
		val devices : List<PciDevice> = listOf(),
        Field
        val cpuArchitecture : String,
        Field
		val totalMemory : Long
		)