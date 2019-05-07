package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.hardware.ChassisInformation
import com.github.kerubistan.kerub.model.hardware.MemoryInformation
import com.github.kerubistan.kerub.model.hardware.PciDevice
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
import com.github.kerubistan.kerub.model.hardware.SystemInformation
import com.github.kerubistan.kerub.model.index.Indexed
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.model.views.Detailed
import com.github.kerubistan.kerub.model.views.Full
import com.github.kerubistan.kerub.model.views.Simple
import com.github.kerubistan.kerub.network.EthernetPort
import com.github.kerubistan.kerub.network.NetworkInterface
import com.github.kerubistan.kerub.utils.validateSize
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
		val blockDevices: List<BlockDevice> = listOf(),
		@Field
		val networkPorts : List<EthernetPort> = listOf(),
		@Field
		val networkInterfaces : List<NetworkInterface> = listOf(),
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
		val hypervisorCapabilities: List<Any> = listOf()
) : Serializable, Indexed<HostCapabilitiesIndex> {

	@get:JsonIgnore
	override val index: HostCapabilitiesIndex by lazy { HostCapabilitiesIndex(this) }

	init {
		totalMemory.validateSize("totalMemory")
		storageCapabilities.count { it is GvinumStorageCapability }.let { cnt ->
			check(cnt <= 1) { "there can be only one gvinum capability, there are $cnt" }
		}
	}

}