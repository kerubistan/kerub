package com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class UnAllocateFsFactoryTest : AbstractFactoryVerifications(UnAllocateFsFactory) {
	@Test
	fun produce() {
		assertTrue("ro disk, single allocation - do not offer") {
			val host1capability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					fsType = "ext4",
					mountPoint = "/kerub"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "testhost-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1capability
							)
					)
			)
			val fsAllocation = VirtualStorageFsAllocation(
					capabilityId = host1capability.id,
					mountPoint = host1capability.mountPoint,
					actualSize = testCdrom.size,
					hostId = host1.id,
					fileName = "${host1capability.mountPoint}/test.raw",
					type = VirtualDiskFormat.raw
			)
			UnAllocateFsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(fsAllocation)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("rw disk, single allocation - do not offer") {
			val host1capability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					fsType = "ext4",
					mountPoint = "/kerub"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "testhost-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1capability
							)
					)
			)
			val fsAllocation = VirtualStorageFsAllocation(
					capabilityId = host1capability.id,
					mountPoint = host1capability.mountPoint,
					actualSize = testDisk.size,
					hostId = host1.id,
					fileName = "${host1capability.mountPoint}/test.raw",
					type = VirtualDiskFormat.raw
			)
			UnAllocateFsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(fsAllocation)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("rw disk is being recycled - offer to delete") {
			val host1capability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					fsType = "ext4",
					mountPoint = "/kerub"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "testhost-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1capability
							)
					)
			)
			val disk = testDisk.copy(
					recycling = true
			)
			val fsAllocation = VirtualStorageFsAllocation(
					capabilityId = host1capability.id,
					mountPoint = host1capability.mountPoint,
					actualSize = disk.size,
					hostId = host1.id,
					fileName = "${host1capability.mountPoint}/test.raw",
					type = VirtualDiskFormat.raw
			)
			UnAllocateFsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(disk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = disk.id,
											allocations = listOf(fsAllocation)
									)
							)
					)
			).single() == UnAllocateFs(vstorage = disk, allocation = fsAllocation, host = host1)
		}
		assertTrue("ro disk have a duplicate - offer to delete") {
			val host1capability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					fsType = "ext4",
					mountPoint = "/kerub"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "testhost-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1capability
							)
					)
			)
			val host2capability = LvmStorageCapability(
					size = 1.TB,
					id = UUID.randomUUID(),
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					)
			)
			val host2 = testHost.copy(
					id = UUID.randomUUID(),
					address = "testhost-2.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host2capability
							),
							blockDevices = listOf(
									BlockDevice(
											deviceName = "/dev/sdb",
											storageCapacity = 1.TB
									)
							)
					)
			)
			val fsAllocation = VirtualStorageFsAllocation(
					capabilityId = host1capability.id,
					mountPoint = host1capability.mountPoint,
					actualSize = testCdrom.size,
					hostId = host1.id,
					fileName = "${host1capability.mountPoint}/test.raw",
					type = VirtualDiskFormat.raw
			)
			val lvmAllocation = VirtualStorageLvmAllocation(
					hostId = host2.id,
					capabilityId = host2capability.id,
					actualSize = testCdrom.size,
					vgName = host2capability.volumeGroupName,
					path = "/dev/vg-1/testdisk"
			)
			UnAllocateFsFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(hostUp(host1), hostUp(host2)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(fsAllocation, lvmAllocation)
									)
							)
					)
			).single() == UnAllocateFs(vstorage = testCdrom, allocation = fsAllocation, host = host1)
		}
	}
}