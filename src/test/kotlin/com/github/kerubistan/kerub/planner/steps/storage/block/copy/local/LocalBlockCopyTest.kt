package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.HostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocalBlockCopyTest : OperationalStepVerifications() {
	override val step = LocalBlockCopy(
			sourceDevice = testDisk,
			targetDevice = testDisk.copy(id = randomUUID()),
			sourceAllocation = VirtualStorageLvmAllocation(
					capabilityId = testLvmCapability.id,
					actualSize = testDisk.size,
					path = "",
					vgName = testLvmCapability.volumeGroupName,
					hostId = testHost.id
			),
			allocationStep = CreateLv(
					host = testHost,
					disk = testDisk,
					capability = testLvmCapability
			)
	)


	@Test
	fun validate() {
		assertThrows<IllegalStateException>("same source and target") {
			LocalBlockCopy(
					sourceDevice = testDisk,
					targetDevice = testDisk,
					sourceAllocation = VirtualStorageLvmAllocation(
							capabilityId = testLvmCapability.id,
							actualSize = testDisk.size,
							path = "",
							vgName = testLvmCapability.volumeGroupName,
							hostId = testHost.id
					),
					allocationStep = CreateLv(
							host = testHost,
							disk = testDisk,
							capability = testLvmCapability
					)
			)
		}
		assertThrows<IllegalStateException>("different hosts") {
			LocalBlockCopy(
					sourceDevice = testDisk,
					targetDevice = testDisk.copy(id = randomUUID()),
					sourceAllocation = VirtualStorageLvmAllocation(
							capabilityId = testLvmCapability.id,
							actualSize = testDisk.size,
							path = "",
							vgName = testLvmCapability.volumeGroupName,
							hostId = randomUUID()
					),
					allocationStep = CreateLv(
							host = testHost,
							disk = testDisk,
							capability = testLvmCapability
					)
			)
		}
		assertNotNull("All OK") {
			LocalBlockCopy(
					sourceDevice = testDisk,
					targetDevice = testDisk.copy(id = randomUUID()),
					sourceAllocation = VirtualStorageLvmAllocation(
							capabilityId = testLvmCapability.id,
							actualSize = testDisk.size,
							path = "",
							vgName = testLvmCapability.volumeGroupName,
							hostId = testHost.id
					),
					allocationStep = CreateLv(
							host = testHost,
							disk = testDisk,
							capability = testLvmCapability
					)
			)
		}

	}

	@Test
	fun take() {
		assertTrue("basic behavior") {
			val lvmVg1 = LvmStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB
					)
			)
			val lvmVg2 = LvmStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					volumeGroupName = "vg-2",
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(lvmVg1, lvmVg2)
					)
			)
			val targetDisk = testDisk.copy(
					id = randomUUID()
			)
			val state = LocalBlockCopy(
					sourceDevice = testDisk,
					targetDevice = targetDisk,
					allocationStep = CreateLv(capability = lvmVg1, disk = testDisk, host = host),
					sourceAllocation = VirtualStorageLvmAllocation(
							capabilityId = lvmVg1.id,
							actualSize = testDisk.size,
							path = "/dev/vg-1/${testDisk.id}",
							vgName = lvmVg1.volumeGroupName,
							hostId = host.id
					)
			).take(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostUp(host)),
							vStorage = listOf(targetDisk, testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															vgName = lvmVg2.volumeGroupName,
															path = "/dev/${lvmVg2.volumeGroupName}/${testDisk.id}",
															actualSize = testDisk.size,
															capabilityId = lvmVg2.id
													)
											)
									)
							)
					))

			state.vStorage.getValue(targetDisk.id).dynamic!!.allocations.single().let {
				it is VirtualStorageLvmAllocation
						&& it.hostId == host.id
						&& it.capabilityId == lvmVg1.id
			}
		}
	}

	@Test
	fun otherReservations() {
		assertTrue("reservation for the copied disk allocation, non-exclusive reservation for the host") {
			val lvmVg1 = LvmStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB
					)
			)
			val lvmVg2 = LvmStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					volumeGroupName = "vg-2",
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
									BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
							),
							storageCapabilities = listOf(lvmVg1, lvmVg2)
					)
			)
			val targetDisk = testDisk.copy(id = randomUUID())
			LocalBlockCopy(
					sourceDevice = testDisk,
					targetDevice = targetDisk,
					allocationStep = CreateLv(capability = lvmVg1, disk = testDisk, host = host),
					sourceAllocation = VirtualStorageLvmAllocation(
							capabilityId = lvmVg1.id,
							actualSize = testDisk.size,
							path = "/dev/vg-1/${testDisk.id}",
							vgName = lvmVg1.volumeGroupName,
							hostId = host.id
					)
			).reservations().let { reservations ->
				reservations.any {
					it is VirtualStorageReservation && it.device == testDisk
				} && reservations.any {
					it is HostReservation && it.isShared()
				}
			}
		}
	}
}