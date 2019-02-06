package com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class UnAllocateLvFactoryTest : AbstractFactoryVerifications(UnAllocateLvFactory) {
	@Test
	fun produce() {
		assertTrue("a read-only disk with only a single allocation - do not offer to remove") {
			val host1cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val diskAllocationHost1 = VirtualStorageLvmAllocation(
					hostId = host1.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host1cap.id
			)
			UnAllocateLvFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(
													diskAllocationHost1
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("a read-write disk with only a single allocation - do not offer to remove") {
			val host1cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val diskAllocationHost1 = VirtualStorageLvmAllocation(
					hostId = host1.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host1cap.id
			)
			UnAllocateLvFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													diskAllocationHost1
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("a read-only disk with two instances - offer both") {
			val host1cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val host2cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host2 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-2.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val diskAllocationHost1 = VirtualStorageLvmAllocation(
					hostId = host1.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host1cap.id
			)
			val diskAllocationHost2 = VirtualStorageLvmAllocation(
					hostId = host2.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host2cap.id
			)
			UnAllocateLvFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(hostUp(host1), hostUp(host2)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(
													diskAllocationHost1, diskAllocationHost2
											)
									)
							)
					)
			).let {
				it.contains(UnAllocateLv(
						host = host1,
						vstorage = testCdrom,
						allocation = diskAllocationHost1
				))
				&& it.contains(UnAllocateLv(
						host = host2,
						vstorage = testCdrom,
						allocation = diskAllocationHost2
				))
			}
		}
		assertTrue("A read-only disk with two allocations - only one of the hosts are up") {
			val host1cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val host2cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host2 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-2.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val diskAllocationHost1 = VirtualStorageLvmAllocation(
					hostId = host1.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host1cap.id
			)
			val diskAllocationHost2 = VirtualStorageLvmAllocation(
					hostId = host2.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host2cap.id
			)
			UnAllocateLvFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(testCdrom),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testCdrom.id,
											allocations = listOf(
													diskAllocationHost1, diskAllocationHost2
											)
									)
							)
					)
			) == listOf(
					// because host2 is down
					UnAllocateLv(
							host = host1,
							vstorage = testCdrom,
							allocation = diskAllocationHost1
					)
			)
		}
		assertTrue("a recycling disk") {
			val host1cap = LvmStorageCapability(
					id = UUID.randomUUID(),
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					),
					size = 1.TB,
					volumeGroupName = "vg1"
			)
			val host1 = testHost.copy(
					id = UUID.randomUUID(),
					address = "host-1.example.com",
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									host1cap
							)
					)
			)
			val recyclingDisk = testDisk.copy(recycling = true)
			val diskAllocation = VirtualStorageLvmAllocation(
					hostId = host1.id,
					path = "/dev/vg1/testdisk1",
					vgName = "vg1",
					actualSize = 1.GB,
					capabilityId = host1cap.id
			)
			UnAllocateLvFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(hostUp(host1)),
							vStorage = listOf(
									recyclingDisk
							),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = recyclingDisk.id,
											allocations = listOf(
													diskAllocation
											)
									)
							)
					)
			) == listOf(
					UnAllocateLv(
							host = host1,
							vstorage = recyclingDisk,
							allocation = diskAllocation
					)
			)
		}
	}
}