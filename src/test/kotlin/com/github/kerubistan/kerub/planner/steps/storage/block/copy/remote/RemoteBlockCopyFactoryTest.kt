package com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class RemoteBlockCopyFactoryTest : AbstractFactoryVerifications(RemoteBlockCopyFactory) {

	@Test
	fun produce() {
		assertTrue("lvm, enough free space, lvm capability given, let's do it") {
			val targetCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB
					),
					size = 2.TB,
					volumeGroupName = "vg-1"
			)
			val targetHost = testHost.copy(
					id = UUID.randomUUID(),
					address = "target.example.com",
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(
									targetCapability
							)
					)
			)

			val sourceCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB
					),
					size = 2.TB,
					volumeGroupName = "vg-1"
			)
			val sourceHost = testHost.copy(
					id = UUID.randomUUID(),
					address = "source.example.com",
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(
									sourceCapability
							)
					)
			)

			val sourceDisk = testDisk.copy(
					id = UUID.randomUUID(),
					name = "source-disk"
			)
			val targetDisk = testDisk.copy(
					id = UUID.randomUUID(),
					name = "target-disk",
					expectations = listOf(
							CloneOfStorageExpectation(sourceStorageId = sourceDisk.id)
					)
			)
			val sourceDiskAllocation = VirtualStorageLvmAllocation(
					hostId = sourceHost.id,
					vgName = sourceCapability.volumeGroupName,
					path = "/dev/${sourceCapability.volumeGroupName}/${sourceDisk.id}",
					actualSize = sourceDisk.size,
					capabilityId = sourceCapability.id
			)
			val steps = RemoteBlockCopyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targetHost),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "source-key"
									),
									HostConfiguration(
											id = targetHost.id,
											acceptedPublicKeys = listOf("source-key")
									)
							),
							hostDyns = listOf(
									hostUp(sourceHost).copy(
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(
															id = sourceCapability.id,
															freeCapacity = 1.TB)
											)
									),
									hostUp(targetHost).copy(
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(
															id = targetCapability.id,
															freeCapacity = 2.TB)
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