package com.github.kerubistan.kerub.planner.steps.vstorage.fs.truncate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertTrue

internal class TruncateImageFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state") {
			TruncateImageFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("no disks - no steps") {
			TruncateImageFactory.produce(
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
							storageCapabilities = listOf(capability)
					)
			)
			TruncateImageFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = host.id, status = HostStatus.Up)),
							vStorage = listOf(disk)
					)
			).single() == TruncateImage(disk = disk, host = host,
					allocation = VirtualStorageFsAllocation(
							type = VirtualDiskFormat.raw,
							mountPoint = "/kerub",
							fileName = "${disk.id}.raw",
							actualSize = 0.GB,
							hostId = host.id
					)
			)
		}

	}
}