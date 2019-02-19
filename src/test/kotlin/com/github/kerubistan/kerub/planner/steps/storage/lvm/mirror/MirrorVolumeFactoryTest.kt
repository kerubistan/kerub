package com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

@ExperimentalUnsignedTypes
class MirrorVolumeFactoryTest : AbstractFactoryVerifications(MirrorVolumeFactory) {

	@Test
	fun produce() {
		assertTrue("there is only one device in the VG, not enough to mirror") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice("/dev/sda", 1.TB)),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 1.TB
							)
					)
			)
			MirrorVolumeFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															mirrors = 0,
															actualSize = testDisk.size,
															path = "",
															vgName = lvmStorageCapability.volumeGroupName,
															capabilityId = lvmStorageCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("host has two PVs in the VG, but it is down") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice("/dev/sda", 1.TB)),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			MirrorVolumeFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															mirrors = 0,
															actualSize = testDisk.size,
															path = "",
															vgName = lvmStorageCapability.volumeGroupName,
															capabilityId = lvmStorageCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("host has two PVs in the VG, but not enough space for duplicate") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
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
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB)
							),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 0.TB
							)
					)
			)
			MirrorVolumeFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															mirrors = 0,
															actualSize = testDisk.size,
															path = "",
															vgName = lvmStorageCapability.volumeGroupName,
															capabilityId = lvmStorageCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("host has two PVs in the VG, all running - let's offer to mirror") {
			val lvmStorageCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
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
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB)
							),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 1500.GB
							)
					)
			)
			MirrorVolumeFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															mirrors = 0,
															actualSize = testDisk.size,
															path = "",
															vgName = lvmStorageCapability.volumeGroupName,
															capabilityId = lvmStorageCapability.id
													)
											)
									)
							)
					)
			).let {
				it.isNotEmpty()
						&& it.map { it.capability.id }.toSet() == setOf(lvmStorageCapability.id)
						&& it.map { it.mirrors.toInt() }.toSet() == setOf(1)  // because only two disks
			}
		}
		assertTrue("host has 6 PVs in the VG, all running, lvm already mirrored - can offer less mirrors") {
			val lvmStorageCapability = LvmStorageCapability(
				id = UUID.randomUUID(),
				physicalVolumes = mapOf(
						"/dev/sda" to 1.TB,
						"/dev/sdb" to 1.TB,
						"/dev/sdc" to 1.TB,
						"/dev/sdd" to 1.TB,
						"/dev/sde" to 1.TB,
						"/dev/sdf" to 1.TB
				),
				size = 6.TB,
				volumeGroupName = "vg-1"
		)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB),
									BlockDevice("/dev/sdc", 1.TB),
									BlockDevice("/dev/sdd", 1.TB),
									BlockDevice("/dev/sde", 1.TB),
									BlockDevice("/dev/sdf", 1.TB)
							),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 1500.GB
							)
					)
			)
			MirrorVolumeFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															mirrors = 4,
															actualSize = testDisk.size,
															path = "",
															vgName = lvmStorageCapability.volumeGroupName,
															capabilityId = lvmStorageCapability.id
													)
											)
									)
							)
					)
			).let {
				it.isNotEmpty()
						&& it.map { it.capability.id }.toSet() == setOf(lvmStorageCapability.id)
						&& it.map { it.mirrors.toInt() }.toSet() == (0..3).toSet()
			}

		}
	}

}