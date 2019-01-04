package com.github.kerubistan.kerub.planner.steps.vstorage.fs.create

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertTrue

class CreateImageFactoryTest : AbstractFactoryVerifications(CreateImageFactory) {
	@Test
	fun produce() {
		assertTrue("no disks - no steps") {
			CreateImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up))
					)
			).isEmpty()
		}
		assertTrue("one disk, availability required - generate step") {
			val disk = testDisk.copy(
					expectations = listOf(StorageAvailabilityExpectation())
			)
			val capability = FsStorageCapability(mountPoint = "/kerub", fsType = "ext4", size = 100.GB)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							distribution = SoftwarePackage("Ubuntu", Version.fromVersionString("12.04")),
							storageCapabilities = listOf(capability),
							installedSoftware = listOf(
									SoftwarePackage(
											name = "qemu-utils",
											version = Version.fromVersionString("1.2.12")
									)
							)
					)
			)
			CreateImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vStorage = listOf(disk)
					)
			) == listOf(CreateImage(disk = disk, host = host, format = VirtualDiskFormat.raw, capability = capability))
		}
	}
}