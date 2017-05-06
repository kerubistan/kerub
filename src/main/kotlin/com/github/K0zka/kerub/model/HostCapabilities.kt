package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.hardware.MemoryInformation
import com.github.K0zka.kerub.model.hardware.PciDevice
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.model.views.Full
import com.github.K0zka.kerub.model.views.Simple
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.math.BigInteger

data class HostCapabilities(
		@JsonView(Simple::class)
		@Field
		val os: OperatingSystem? = null,
		@JsonView(Simple::class)
		@Field
		val distribution: SoftwarePackage? = null,
		@JsonView(Full::class)
		@Field
		val installedSoftware: List<SoftwarePackage> = listOf(),
		@JsonView(Detailed::class)
		@Field
		val devices: List<PciDevice> = listOf(),
		@JsonView(Simple::class)
		@Field
		val cpuArchitecture: String,
		@JsonView(Simple::class)
		@Field
		val totalMemory: BigInteger,
		@JsonView(Simple::class)
		@Field
		val memoryDevices: List<MemoryInformation> = listOf(),
		@JsonView(Detailed::class)
		@Field
		val system: SystemInformation? = null,
		@Field
		@JsonView(Detailed::class)
		val cpus: List<ProcessorInformation> = listOf(),
		@JsonView(Detailed::class)
		@Field
		val chassis: ChassisInformation? = null,
		@Field
		@JsonView(Detailed::class)
		val powerManagment: List<PowerManagementInfo> = listOf(),
		@Field
		@JsonView(Detailed::class)
		val storageCapabilities: List<StorageCapability> = listOf(),
		@Field
		@JsonView(Detailed::class)
		val hypervisorCapabilities : List<Any> = listOf()
)
: Serializable