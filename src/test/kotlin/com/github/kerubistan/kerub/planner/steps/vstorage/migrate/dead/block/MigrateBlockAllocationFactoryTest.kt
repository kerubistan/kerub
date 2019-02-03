package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.dead.block

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class MigrateBlockAllocationFactoryTest : AbstractFactoryVerifications(MigrateBlockAllocationFactory) {

	@Test
	fun produce() {

		assertTrue("only a single host - nowhere to migrate to") {
			val host1cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host1 = testHost.copy(
					address = "host-1.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host1cap)
					)
			)
			val host1dyn = hostUp(host1).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host1cap.id,
									freeCapacity = 1.TB
							)
					)
			)
			val sourceAllocation = VirtualStorageLvmAllocation(
					hostId = host1.id,
					vgName = host1cap.volumeGroupName,
					capabilityId = host1cap.id,
					path = "/dev/vg-1/source",
					actualSize = 1.GB
			)
			MigrateBlockAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1),
							hostDyns = listOf(host1dyn),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													sourceAllocation
											)
									)
							)
					)).isEmpty()
		}

		assertTrue("two servers, plenty of space, rw disk - but ut is used by a vm - let's do nothing") {
			val host1cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host1 = testHost.copy(
					address = "host-1.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host1cap)
					)
			)
			val host2cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host2 = testHost.copy(
					address = "host-2.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host2cap)
					)
			)
			val host1dyn = hostUp(host1).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host1cap.id,
									freeCapacity = 1.TB
							)
					)
			)
			val host2dyn = hostUp(host2).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host2cap.id,
									freeCapacity = 1.TB
							)
					)
			)
			val sourceAllocation = VirtualStorageLvmAllocation(
					hostId = host1.id,
					vgName = host1cap.volumeGroupName,
					capabilityId = host1cap.id,
					path = "/dev/vg-1/source",
					actualSize = 1.GB
			)
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = testDisk.id,
									device = DeviceType.disk,
									readOnly = false,
									bus = BusType.sata
							)
					)
			)
			MigrateBlockAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(host1dyn, host2dyn),
							vms = listOf(vm),
							vmDyns = listOf(
									VirtualMachineDynamic(
											id = vm.id,
											status = VirtualMachineStatus.Up,
											memoryUsed = 1.GB,
											hostId = host1.id
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													sourceAllocation
											)
									)
							)
					)).isEmpty()
		}

		assertTrue("two servers, rw disk - but not enough free space on the other server - let's do nothing") {
			val host1cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host1 = testHost.copy(
					address = "host-1.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host1cap)
					)
			)
			val host2cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host2 = testHost.copy(
					address = "host-2.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host2cap)
					)
			)
			val host1dyn = hostUp(host1).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host1cap.id,
									freeCapacity = 1.TB
							)
					)
			)
			val host2dyn = hostUp(host2).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host2cap.id,
									freeCapacity = 0.TB
							)
					)
			)
			val sourceAllocation = VirtualStorageLvmAllocation(
					hostId = host1.id,
					vgName = host1cap.volumeGroupName,
					capabilityId = host1cap.id,
					path = "/dev/vg-1/source",
					actualSize = 1.GB
			)
			MigrateBlockAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(host1dyn, host2dyn),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													sourceAllocation
											)
									)
							)
					)).isEmpty()
		}

		assertTrue("two servers, plenty of space, rw disk - let's do it") {
			val host1cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host1 = testHost.copy(
					address = "host-1.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host1cap)
					)
			)
			val host2cap = LvmStorageCapability(
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					size = 1.TB,
					volumeGroupName = "vg-1"
			)
			val host2 = testHost.copy(
					address = "host-2.example.com",
					id = UUID.randomUUID(),
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
							storageCapabilities = listOf(host2cap)
					)
			)
			val host1dyn = hostUp(host1).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host1cap.id,
									freeCapacity = 1.TB
							)
					)
			)
			val host2dyn = hostUp(host2).copy(
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = host2cap.id,
									freeCapacity = 1.TB
							)
					)
			)
			val sourceAllocation = VirtualStorageLvmAllocation(
					hostId = host1.id,
					vgName = host1cap.volumeGroupName,
					capabilityId = host1cap.id,
					path = "/dev/vg-1/source",
					actualSize = 1.GB
			)
			MigrateBlockAllocationFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(host1dyn, host2dyn),
							hostCfgs = listOf(
									HostConfiguration(id = host1.id, publicKey = "host1-pibkey"),
									HostConfiguration(id = host2.id, acceptedPublicKeys = listOf("host1-pibkey"))
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													sourceAllocation
											)
									)
							)
					)).single().let {
				it.sourceAllocation == sourceAllocation
						&& it.sourceHost == host1
						&& it.targetHost == host2
						&& it.allocationStep.host == host2
						&& it.allocationStep.capability == host2cap
						&& it.deAllocationStep.host == host1
						&& it.deAllocationStep.allocation == sourceAllocation
						&& it.virtualStorage == testDisk
			}
		}

	}


}