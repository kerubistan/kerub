package com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdCapabilities
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import kotlin.test.assertTrue

class UnAllocateGvinumFactoryTest : AbstractFactoryVerifications(UnAllocateGvinumFactory) {

	@Test
	fun produce() {
		assertTrue("rw disk recycling - expecting an offer to remove") {
			val host1cap = GvinumStorageCapability(
					devices = listOf(
							GvinumStorageCapabilityDrive(
									size = 1.TB,
									device = "/dev/whatever1",
									name = "gvinumdsik-1"
							)
					)
			)
			val host = testFreeBsdHost.copy(
					capabilities = testFreeBsdCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							),
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/whatever1", storageCapacity = 1.TB)
							)
					)
			)
			val disk = testDisk.copy(recycling = true)
			val allocation = VirtualStorageGvinumAllocation(
					hostId = host.id,
					capabilityId = host1cap.id,
					actualSize = disk.size,
					configuration = SimpleGvinumConfiguration(
							diskName = host1cap.devices.single().name
					)
			)
			UnAllocateGvinumFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(disk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = disk.id,
											allocations = listOf(allocation)
									)
							)
					)
			).single() == UnAllocateGvinum(vstorage = disk, allocation = allocation, host = host)
		}
		assertTrue("ro disk with multiple allocations - expecting an offer to remove") {
			val host1cap = GvinumStorageCapability(
					devices = listOf(
							GvinumStorageCapabilityDrive(
									size = 1.TB,
									device = "/dev/whatever1",
									name = "gvinumdsik-1"
							)
					)
			)
			val host = testFreeBsdHost.copy(
					capabilities = testFreeBsdCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							),
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/whatever1", storageCapacity = 1.TB)
							)
					)
			)
			val otherCapability = FsStorageCapability(
					size = 1.TB,
					mountPoint = "/kerub",
					fsType = "ext4"
			)
			val otherHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									otherCapability
							)
					)
			)
			val allocation = VirtualStorageGvinumAllocation(
					hostId = host.id,
					capabilityId = host1cap.id,
					actualSize = testCdrom.size,
					configuration = SimpleGvinumConfiguration(
							diskName = host1cap.devices.single().name
					)
			)
			val otherAllocation = VirtualStorageFsAllocation(
					hostId = otherHost.id,
					capabilityId = otherCapability.id,
					mountPoint = otherCapability.mountPoint,
					actualSize = testCdrom.size,
					type = VirtualDiskFormat.raw,
					fileName = "${otherCapability.mountPoint}/cdrom.raw"
			)
			UnAllocateGvinumFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host, otherHost),
							hostDyns = listOf(
									hostUp(host), hostUp(otherHost)
							),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(allocation, otherAllocation)
									)
							)
					)
			).single() == UnAllocateGvinum(vstorage = testCdrom, host = host, allocation = allocation)
		}
		assertTrue("ro disk with single allocation - do not offer to remove") {
			val host1cap = GvinumStorageCapability(
					devices = listOf(
							GvinumStorageCapabilityDrive(
									size = 1.TB,
									device = "/dev/whatever1",
									name = "gvinumdsik-1"
							)
					)
			)
			val host = testFreeBsdHost.copy(
					capabilities = testFreeBsdCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							),
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/whatever1", storageCapacity = 1.TB)
							)
					)
			)
			val allocation = VirtualStorageGvinumAllocation(
					hostId = host.id,
					capabilityId = host1cap.id,
					actualSize = testCdrom.size,
					configuration = SimpleGvinumConfiguration(
							diskName = host1cap.devices.single().name
					)
			)
			UnAllocateGvinumFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(allocation)
									)
							)
					)
			).isEmpty()
		}
	}
}