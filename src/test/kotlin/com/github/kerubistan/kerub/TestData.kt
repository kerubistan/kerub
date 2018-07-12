package com.github.kerubistan.kerub

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
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