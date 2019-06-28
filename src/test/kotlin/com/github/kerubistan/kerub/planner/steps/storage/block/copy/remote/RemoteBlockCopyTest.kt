package com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.utils.hasAny
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class RemoteBlockCopyTest {

	@Test
	fun validate() {
		assertThrows<IllegalStateException>("source and target device are the same") {
			val sourceHost = testHost.copy(
					id = randomUUID()
			)
			val targetHost = testHost.copy(
					id = randomUUID()
			)
			val sourceCapability = testLvmCapability
			val targetCapability = testLvmCapability.copy(
					id = randomUUID()
			)
			val sourceDisk = testDisk.copy(id = randomUUID())
			val targetDisk = testDisk.copy(id = randomUUID())
			RemoteBlockCopy(
					sourceDevice = sourceDisk,
					targetDevice = sourceDisk,
					sourceAllocation = VirtualStorageLvmAllocation(
							hostId = sourceHost.id,
							capabilityId = sourceCapability.id,
							path = "",
							actualSize = sourceDisk.size,
							vgName = sourceCapability.volumeGroupName
					),
					sourceHost = sourceHost,
					allocationStep = CreateLv(
							host = targetHost,
							disk = targetDisk,
							capability = targetCapability
					)
			)
		}
		assertThrows<IllegalStateException>("source and target host are the same") {
			val sourceHost = testHost.copy(
					id = randomUUID()
			)
			val targetHost = testHost.copy(
					id = randomUUID()
			)
			val sourceCapability = testLvmCapability
			val targetCapability = testLvmCapability.copy(
					id = randomUUID()
			)
			val sourceDisk = testDisk.copy(id = randomUUID())
			val targetDisk = testDisk.copy(id = randomUUID())
			RemoteBlockCopy(
					sourceDevice = sourceDisk,
					targetDevice = targetDisk,
					sourceAllocation = VirtualStorageLvmAllocation(
							hostId = sourceHost.id,
							capabilityId = sourceCapability.id,
							path = "",
							actualSize = sourceDisk.size,
							vgName = sourceCapability.volumeGroupName
					),
					sourceHost = targetHost,
					allocationStep = CreateLv(
							host = targetHost,
							disk = targetDisk,
							capability = targetCapability
					)
			)
		}
	}

	@Test
	fun take() {
		val sourceCapability = LvmStorageCapability(
				id = randomUUID(),
				size = 1.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf(
						"/dev/sda" to 1.TB
				)
		)
		val sourceHost = testHost.copy(
				id = randomUUID(),
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
						storageCapabilities = listOf(sourceCapability)
				)
		)
		val targetCapability = LvmStorageCapability(
				id = randomUUID(),
				size = 1.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf(
						"/dev/sda" to 1.TB
				)
		)
		val targetHost = testHost.copy(
				id = randomUUID(),
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)),
						storageCapabilities = listOf(targetCapability)
				)
		)
		val sourceDisk = testDisk.copy(id = randomUUID())
		val targetDisk = testDisk.copy(id = randomUUID())
		val state = RemoteBlockCopy(
				sourceDevice = sourceDisk,
				targetDevice = targetDisk,
				sourceAllocation = VirtualStorageLvmAllocation(
						hostId = sourceHost.id,
						capabilityId = sourceCapability.id,
						path = "",
						actualSize = sourceDisk.size,
						vgName = sourceCapability.volumeGroupName
				),
				sourceHost = sourceHost,
				allocationStep = CreateLv(
						host = targetHost,
						disk = targetDisk,
						capability = targetCapability
				)
		).take(
				OperationalState.fromLists(
						hosts = listOf(sourceHost, targetHost),
						hostDyns = listOf(
								hostUp(sourceHost),
								hostUp(targetHost)
						),
						vStorage = listOf(sourceDisk, targetDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = sourceDisk.id,
										allocations = listOf(
												VirtualStorageLvmAllocation(
														hostId = sourceHost.id,
														vgName = sourceCapability.volumeGroupName,
														capabilityId = sourceCapability.id,
														actualSize = sourceDisk.size,
														path = ""
												)
										)
								)
						)
				)
		)

		assertTrue {
			state.vStorage.getValue(targetDisk.id).dynamic!!.allocations.any {
				it is VirtualStorageLvmAllocation &&
						it.hostId == targetHost.id &&
						it.capabilityId == targetCapability.id &&
						it.vgName == targetCapability.volumeGroupName

			}
		}

	}

	@Test
	fun reservations() {
		assertTrue("check reservation for source and target vdisk and host") {
			val sourceHost = testHost.copy(
					id = randomUUID()
			)
			val targetHost = testHost.copy(
					id = randomUUID()
			)
			val sourceCapability = testLvmCapability
			val targetCapability = testLvmCapability.copy(
					id = randomUUID()
			)
			val sourceDisk = testDisk.copy(id = randomUUID())
			val targetDisk = testDisk.copy(id = randomUUID())
			RemoteBlockCopy(
					sourceDevice = sourceDisk,
					targetDevice = targetDisk,
					sourceAllocation = VirtualStorageLvmAllocation(
							hostId = sourceHost.id,
							capabilityId = sourceCapability.id,
							path = "",
							actualSize = sourceDisk.size,
							vgName = sourceCapability.volumeGroupName
					),
					sourceHost = sourceHost,
					allocationStep = CreateLv(
							host = targetHost,
							disk = targetDisk,
							capability = targetCapability
					)
			).reservations().let { reservations ->
				reservations.hasAny<UseHostReservation> { it.host == targetHost } &&
						reservations.hasAny<UseHostReservation> { it.host == sourceHost } &&
						reservations.hasAny<VirtualStorageReservation> { it.device == targetDisk } &&
						reservations.hasAny<VirtualStorageReservation> { it.device == sourceDisk }
			}

		}
	}
}