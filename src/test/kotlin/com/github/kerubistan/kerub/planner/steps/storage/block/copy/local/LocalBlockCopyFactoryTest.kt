package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdCapabilities
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.KB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class LocalBlockCopyFactoryTest : AbstractFactoryVerifications(LocalBlockCopyFactory) {

	@Test
	fun produce() {

		assertTrue("no free capacity") {
			val lvmCapability = LvmStorageCapability(
					id = randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB
					),
					size = 2.TB,
					volumeGroupName = "vg-1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(
									lvmCapability
							)
					)
			)
			val sourceDisk = testDisk.copy(
					id = randomUUID(),
					name = "source-disk"
			)
			val targetDisk = testDisk.copy(
					id = randomUUID(),
					name = "target-disk",
					expectations = listOf(
							CloneOfStorageExpectation(sourceStorageId = sourceDisk.id)
					)
			)
			val sourceDiskAllocation = VirtualStorageLvmAllocation(
					hostId = host.id,
					vgName = lvmCapability.volumeGroupName,
					path = "/dev/${lvmCapability.volumeGroupName}/${sourceDisk.id}",
					actualSize = sourceDisk.size,
					capabilityId = lvmCapability.id
			)
			val steps = LocalBlockCopyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									hostUp(host).copy(
											storageStatus = listOf(
													CompositeStorageDeviceDynamic(
															id = lvmCapability.id,
															reportedFreeCapacity = 10.KB
													)
											)
									)),
							vStorage = listOf(sourceDisk, targetDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = sourceDisk.id,
											allocations = listOf(
													sourceDiskAllocation
											)
									)
							)
					)
			)

			steps.isEmpty()

		}

		assertTrue("lvm, enough free space, lvm capability given, let's do it") {
			val lvmCapability = LvmStorageCapability(
					id = randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB
					),
					size = 2.TB,
					volumeGroupName = "vg-1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(
									lvmCapability
							)
					)
			)
			val sourceDisk = testDisk.copy(
					id = randomUUID(),
					name = "source-disk"
			)
			val targetDisk = testDisk.copy(
					id = randomUUID(),
					name = "target-disk",
					expectations = listOf(
							CloneOfStorageExpectation(sourceStorageId = sourceDisk.id)
					)
			)
			val sourceDiskAllocation = VirtualStorageLvmAllocation(
					hostId = host.id,
					vgName = lvmCapability.volumeGroupName,
					path = "/dev/${lvmCapability.volumeGroupName}/${sourceDisk.id}",
					actualSize = sourceDisk.size,
					capabilityId = lvmCapability.id
			)
			val steps = LocalBlockCopyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									hostUp(host).copy(
											storageStatus = listOf(
													CompositeStorageDeviceDynamic(
															id = lvmCapability.id,
															reportedFreeCapacity = 2.TB
													)
											)
									)),
							vStorage = listOf(sourceDisk, targetDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = sourceDisk.id,
											allocations = listOf(
													sourceDiskAllocation
											)
									)
							)
					)
			)

			steps.any {
				it.sourceDevice == sourceDisk
						&& it.targetDevice == targetDisk
						&& it.sourceAllocation == sourceDiskAllocation
			}

		}

		assertTrue("gvinum, enough free space, lvm capability given, let's do it") {
			val gvinumCapability = GvinumStorageCapability(
					id = randomUUID(),
					devices = listOf(
							GvinumStorageCapabilityDrive(name = "sda", size = 1.TB, device = "/dev/sda"),
							GvinumStorageCapabilityDrive(name = "sdb", size = 1.TB, device = "/dev/sdb")
					)
			)
			val host = testFreeBsdHost.copy(
					capabilities = testFreeBsdCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(
									gvinumCapability
							)
					)
			)
			val sourceDisk = testDisk.copy(
					id = randomUUID(),
					name = "source-disk"
			)
			val targetDisk = testDisk.copy(
					id = randomUUID(),
					name = "target-disk",
					expectations = listOf(
							CloneOfStorageExpectation(sourceStorageId = sourceDisk.id)
					)
			)
			val sourceDiskAllocation = VirtualStorageGvinumAllocation(
					hostId = host.id,
					configuration = SimpleGvinumConfiguration(diskName = "sda"),
					actualSize = sourceDisk.size,
					capabilityId = gvinumCapability.id
			)
			val steps = LocalBlockCopyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									hostUp(host).copy(
											storageStatus = listOf(
													CompositeStorageDeviceDynamic(
															id = gvinumCapability.id,
															reportedFreeCapacity = 2.TB
													)
											)
									)),
							vStorage = listOf(sourceDisk, targetDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = sourceDisk.id,
											allocations = listOf(
													sourceDiskAllocation
											)
									)
							)
					)
			)

			steps.any {
				it.sourceDevice == sourceDisk
						&& it.targetDevice == targetDisk
						&& it.sourceAllocation == sourceDiskAllocation
			}

		}

	}
}