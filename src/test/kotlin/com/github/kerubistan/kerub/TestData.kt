package com.github.kerubistan.kerub

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import io.github.kerubistan.kroki.time.now
import java.util.UUID.randomUUID

val testCpu = ProcessorInformation(
		manufacturer = "Test Corporation",
		coreCount = 1,
		threadCount = 2,
		socket = "Socket-A",
		flags = listOf("vtx", "vtd"),
		version = "latest-greatest"
)

val testHost = Host(
		id = randomUUID(),
		address = "host-1.example.com",
		dedicated = true,
		publicKey = ""
)

val testOtherHost = Host(
		id = randomUUID(),
		address = "host-2.example.com",
		dedicated = true,
		publicKey = ""
)

val testHostCapabilities = HostCapabilities(
		cpuArchitecture = "X86_64",
		totalMemory = 32.GB
)

val testFreeBsdCapabilities = HostCapabilities(
		cpuArchitecture = "X86_64",
		totalMemory = 32.GB,
		os = OperatingSystem.BSD,
		distribution = SoftwarePackage("FreeBSD", Version.fromVersionString("12.0"))
)

val testLvmCapability = LvmStorageCapability(
		id = randomUUID(),
		size = 2.TB,
		physicalVolumes = mapOf("/dev/sda1" to 2.TB),
		volumeGroupName = "test-vg"
)

val testFsCapability = FsStorageCapability(
		id = randomUUID(),
		size = 2.TB,
		mountPoint = "/kerub",
		fsType = "ext4"
)

val testFreeBsdHost = Host(
		id = randomUUID(),
		address = "host-1.freebsd.example.com",
		dedicated = true,
		publicKey = "",
		capabilities = testHostCapabilities.copy(
				os = OperatingSystem.BSD,
				distribution = SoftwarePackage("FreeBSD", Version.fromVersionString("11.0"))
		)
)

val testGvinumCapability = GvinumStorageCapability(
		id = randomUUID(),
		devices = listOf(
				GvinumStorageCapabilityDrive(name = "gvinum-disk-1", size = 2.TB, device = "/dev/ada2")
		)
)

val testDisk = VirtualStorageDevice(
		id = randomUUID(),
		name = "test-vm",
		size = 2.GB
)

val testCdrom = VirtualStorageDevice(
		id = randomUUID(),
		readOnly = true,
		name = "crapware-linux_72.10.iso",
		size = 4.GB
)


val testVm = VirtualMachine(
		id = randomUUID(),
		memory = Range(min = 1.GB, max = 2.GB),
		name = "test-vm",
		nrOfCpus = 1
)

val testVirtualDisk = VirtualStorageDevice(
		id = randomUUID(),
		name = "test-disk",
		readOnly = false,
		size = 1.GB
)

val testVirtualNetwork = VirtualNetwork(
		id = randomUUID(),
		name = "test network"
)

val testProcessor = ProcessorInformation(
		manufacturer = "TEST CORP",
		coreCount = 8,
		threadCount = 16,
		flags = listOf(),
		socket = "TEST-SOCKET",
		version = "1.0"
)

fun hostUp(host : Host) = HostDynamic(status = HostStatus.Up, id = host.id, lastUpdated = now())

fun hostDown(host : Host) = HostDynamic(status = HostStatus.Down, id = host.id, lastUpdated = now())

fun vmUp(vm : VirtualMachine, host: Host) = VirtualMachineDynamic(
		id = vm.id,
		status = VirtualMachineStatus.Up,
		memoryUsed = vm.memory.max,
		lastUpdated = now(),
		hostId = host.id
)

fun diskAllocated(vdisk : VirtualStorageDevice, host : Host, capability: StorageCapability) = VirtualStorageDeviceDynamic(
		lastUpdated = now(),
		id = vdisk.id,
		allocations = listOf(
				when(capability) {
					is LvmStorageCapability -> VirtualStorageLvmAllocation(
							hostId = host.id,
							actualSize = vdisk.size,
							capabilityId = capability.id,
							path = "/dev/${capability.volumeGroupName}/${vdisk.id}",
							vgName = capability.volumeGroupName
					)
					else -> TODO("not handled: ${capability.javaClass.name}")
				}
		)
)

fun diskAllocated(vdisk : VirtualStorageDevice, host : Host, capability: FsStorageCapability, format : VirtualDiskFormat) = VirtualStorageDeviceDynamic(
		lastUpdated = now(),
		id = vdisk.id,
		allocations = listOf(
				VirtualStorageFsAllocation(
					hostId = host.id,
						capabilityId = capability.id,
						actualSize = vdisk.size,
						type = format,
						fileName = "${vdisk.id}.$format",
						mountPoint = capability.mountPoint
				)
		)
)