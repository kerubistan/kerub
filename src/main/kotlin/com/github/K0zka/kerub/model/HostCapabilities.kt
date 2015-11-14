package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.hardware.PciDevice
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.model.views.Full
import com.github.K0zka.kerub.model.views.Simple
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.math.BigInteger

public data class HostCapabilities(
		@JsonView(Simple::class)
		@Field
		val os: OperatingSystem?,
		@JsonView(Simple::class)
		@Field
		val distribution: SoftwarePackage?,
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
		@JsonView(Detailed::class)
		@Field
		val system: SystemInformation? = null,
		@Field
		@JsonView(Detailed::class)
		val cpus: List<ProcessorInformation> = listOf(),
		@JsonView(Detailed::class)
		@Field
		val chassis: ChassisInformation? = null,
		/**
		 * Used by wake on lan - however a more precise and detailed
		 * network discovery should move it to it's correct place
		 */
		@Field
		@JsonView(Detailed::class)
		val macAddresses: List<ByteArray> = listOf(),
		@Field
		@JsonView(Detailed::class)
		val storageCapabilities: List<StorageCapability> = listOf()
)
: Serializable