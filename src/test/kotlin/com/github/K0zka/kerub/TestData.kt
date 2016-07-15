package com.github.K0zka.kerub

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.utils.toSize
import java.util.UUID

val testHost = Host(
		id = UUID.randomUUID(),
		address = "host-1.example.com",
		dedicated = true,
		publicKey = ""
)

val testHostCapabilities = HostCapabilities(
		cpuArchitecture = "X86_64",
		totalMemory = "32 GB".toSize()
)

val testFreeBsdHost = Host(
		id = UUID.randomUUID(),
		address = "host-1.freebsd.example.com",
		dedicated = true,
		publicKey = "",
		capabilities = testHostCapabilities.copy(
				os = OperatingSystem.BSD,
				distribution = SoftwarePackage("FreeBSD", Version.fromVersionString("11.0"))
		)
)

val testVm = VirtualMachine(
		id = UUID.randomUUID(),
		memory = Range(min = "1 GB".toSize(), max = "2 GB".toSize()),
		name = "test-vm",
		nrOfCpus = 1
)

val testVirtualDisk = VirtualStorageDevice(
		id = UUID.randomUUID(),
		name = "test-disk",
		readOnly = false,
		size = "1 GB".toSize()
)