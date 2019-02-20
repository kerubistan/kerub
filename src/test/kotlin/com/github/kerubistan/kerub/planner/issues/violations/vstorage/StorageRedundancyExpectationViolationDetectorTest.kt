package com.github.kerubistan.kerub.planner.issues.violations.vstorage

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.expectations.StorageRedundancyExpectation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test

import java.util.UUID.randomUUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StorageRedundancyExpectationViolationDetectorTest {

	@Test
	fun check() {
		assertTrue("read-only disk with redundancy expectation 1, allocated on two different hosts") {
			val host1FsCap = FsStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val host1 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1FsCap
							)
					)
			)
			val host2FsCap = FsStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val host2 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host2FsCap
							)
					)
			)
			StorageRedundancyExpectationViolationDetector.check(
					testCdrom,
					StorageRedundancyExpectation(nrOfCopies = 1),
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(hostUp(host1), hostUp(host2)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = host1FsCap.id,
															mountPoint = host1FsCap.mountPoint,
															actualSize = testCdrom.size,
															hostId = host1.id,
															type = VirtualDiskFormat.raw,
															fileName = "blah.iso"
													),
													VirtualStorageFsAllocation(
															capabilityId = host2FsCap.id,
															mountPoint = host2FsCap.mountPoint,
															actualSize = testCdrom.size,
															hostId = host2.id,
															type = VirtualDiskFormat.raw,
															fileName = "blah.iso"
													)
											)
									)
							)
					)
			)
		}

		assertTrue("read-only disk with redundancy expectation 2 out of the box, allocated on two different hosts") {
			val host1FsCap = FsStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val host1 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1FsCap
							)
					)
			)
			val host2FsCap = FsStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val host2 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host2FsCap
							)
					)
			)
			StorageRedundancyExpectationViolationDetector.check(
					testCdrom,
					StorageRedundancyExpectation(nrOfCopies = 2, outOfBox = true),
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(hostUp(host1), hostUp(host2)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = host1FsCap.id,
															mountPoint = host1FsCap.mountPoint,
															actualSize = testCdrom.size,
															hostId = host1.id,
															type = VirtualDiskFormat.raw,
															fileName = "blah.iso"
													),
													VirtualStorageFsAllocation(
															capabilityId = host2FsCap.id,
															mountPoint = host2FsCap.mountPoint,
															actualSize = testCdrom.size,
															hostId = host2.id,
															type = VirtualDiskFormat.raw,
															fileName = "blah.iso"
													)
											)
									)
							)
					)
			)
		}
		assertTrue("RW disk with redundancy expectation 2, allocated on LVM with 2 mirrors") {
			val lvmStorageCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 4.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB,
							"/dev/sdc" to 1.TB,
							"/dev/sdd" to 1.TB
					)
			)
			val host1 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB),
									BlockDevice("/dev/sdc", 1.TB),
									BlockDevice("/dev/sdd", 1.TB)
							),
							storageCapabilities = listOf(lvmStorageCapability)
					)
			)
			val hostDynamic = hostUp(host1).copy()
			StorageRedundancyExpectationViolationDetector.check(
					testDisk,
					StorageRedundancyExpectation(nrOfCopies = 2),
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															capabilityId = lvmStorageCapability.id,
															hostId = host1.id,
															actualSize = 100.GB,
															vgName = lvmStorageCapability.volumeGroupName,
															path = "",
															mirrors = 2
													)
											)
									)
							)
					)
			)
		}
		assertTrue("RW disk with redundancy expectation 2, allocated on LVM with 3 mirrors - still fine") {
			val lvmStorageCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 4.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB,
							"/dev/sdc" to 1.TB,
							"/dev/sdd" to 1.TB
					)
			)
			val host1 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB),
									BlockDevice("/dev/sdc", 1.TB),
									BlockDevice("/dev/sdd", 1.TB)
							),
							storageCapabilities = listOf(lvmStorageCapability)
					)
			)
			val hostDynamic = hostUp(host1).copy()
			StorageRedundancyExpectationViolationDetector.check(
					testDisk,
					StorageRedundancyExpectation(nrOfCopies = 2),
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															capabilityId = lvmStorageCapability.id,
															hostId = host1.id,
															actualSize = testDisk.size,
															vgName = lvmStorageCapability.volumeGroupName,
															path = "",
															mirrors = 3
													)
											)
									)
							)
					)
			)
		}
		assertFalse("RW disk with redundancy expectation 3, allocated on LVM with 2 mirrors - not good enough") {
			val lvmStorageCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 4.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB,
							"/dev/sdc" to 1.TB,
							"/dev/sdd" to 1.TB
					)
			)
			val host1 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB),
									BlockDevice("/dev/sdc", 1.TB),
									BlockDevice("/dev/sdd", 1.TB)
							),
							storageCapabilities = listOf(lvmStorageCapability)
					)
			)
			val hostDynamic = hostUp(host1).copy()
			StorageRedundancyExpectationViolationDetector.check(
					testDisk,
					StorageRedundancyExpectation(nrOfCopies = 3),
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															capabilityId = lvmStorageCapability.id,
															hostId = host1.id,
															actualSize = testDisk.size,
															vgName = lvmStorageCapability.volumeGroupName,
															path = "",
															mirrors = 2
													)
											)
									)
							)
					)
			)
		}
		assertFalse("RW disk with redundancy expectation 1, on filesystem - fs does not do that (yet?)") {
			val host1FsCap = FsStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val host1 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1FsCap
							)
					)
			)
			val host2FsCap = FsStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val host2 = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host2FsCap
							)
					)
			)
			StorageRedundancyExpectationViolationDetector.check(
					testCdrom,
					StorageRedundancyExpectation(nrOfCopies = 1),
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(hostUp(host1), hostUp(host2)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															capabilityId = host2FsCap.id,
															mountPoint = host2FsCap.mountPoint,
															actualSize = testCdrom.size,
															hostId = host2.id,
															type = VirtualDiskFormat.raw,
															fileName = "blah.iso"
													)
											)
									)
							)
					)
			)
		}
	}
}