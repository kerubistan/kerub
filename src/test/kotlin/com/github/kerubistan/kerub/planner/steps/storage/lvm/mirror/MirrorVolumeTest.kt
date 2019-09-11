package com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertTrue

@ExperimentalUnsignedTypes
class MirrorVolumeTest : OperationalStepVerifications() {

	override val step: AbstractOperationalStep
		get() {
			val lvmStorageCapability = LvmStorageCapability(
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB,
							"/dev/sdc" to 1.TB
					),
					volumeGroupName = "vg-1",
					size = 2.TB,
					id = UUID.randomUUID()
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(lvmStorageCapability)
					)
			)
			return MirrorVolume(
					host = host,
					mirrors = 1.toUShort(),
					allocation = VirtualStorageLvmAllocation(
							capabilityId = lvmStorageCapability.id,
							path = "",
							mirrors = 0,
							vgName = lvmStorageCapability.volumeGroupName,
							actualSize = 1.GB,
							hostId = host.id
					),
					capability = lvmStorageCapability,
					vStorage = testDisk
			)
		}

	@Test
	fun take() {
		val lvmStorageCapability = LvmStorageCapability(
				size = 8.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf(
						"/dev/sda" to 1.TB,
						"/dev/sdb" to 1.TB
				)
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
		val hostDynamic = hostUp(testHost).copy(
				storageStatus = listOf(
						CompositeStorageDeviceDynamic(
								id = lvmStorageCapability.id,
								reportedFreeCapacity = 1.TB
						)
				)
		)
		val newState = MirrorVolume(
				host = host,
				vStorage = testDisk,
				capability = lvmStorageCapability,
				mirrors = 1.toUShort(),
				allocation = VirtualStorageLvmAllocation(
						capabilityId = lvmStorageCapability.id,
						mirrors = 0,
						hostId = host.id,
						vgName = lvmStorageCapability.volumeGroupName,
						path = "",
						actualSize = 1.GB
				)
		).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostDyns = listOf(
								hostDynamic
						),
						vStorage = listOf(testDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testDisk.id,
										allocations = listOf(
												VirtualStorageLvmAllocation(
														capabilityId = lvmStorageCapability.id,
														actualSize = testDisk.size,
														path = "",
														vgName = lvmStorageCapability.volumeGroupName,
														hostId = host.id,
														mirrors = 0
												)
										)
								)
						)
				)
		)

		assertTrue("number of mirrors set") {
			(newState.vStorage.getValue(testDisk.id).dynamic!!.allocations.single() as VirtualStorageLvmAllocation).mirrors == 1.toByte()
		}
		assertTrue("Storage size decreased by the mirrors") {
			newState.hosts.getValue(host.id).dynamic!!.storageStatus.single { it.id == lvmStorageCapability.id }.freeCapacity < 1.TB
		}

	}

	@Test
	fun validation() {
		assertThrows<IllegalStateException>("Not registered LVM volume") {
			MirrorVolume(
					host = testHost,
					vStorage = testDisk,
					capability = testLvmCapability,
					mirrors = 1.toUShort(),
					allocation = VirtualStorageLvmAllocation(
							capabilityId = testLvmCapability.id,
							mirrors = 0,
							hostId = testHost.id,
							vgName = testLvmCapability.volumeGroupName,
							path = "",
							actualSize = 1.GB
					)
			)
		}
		assertThrows<IllegalStateException>("Too many mirrors") {
			val lvmStorageCapability = LvmStorageCapability(
					size = 8.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB,
							"/dev/sdc" to 1.TB,
							"/dev/sdd" to 1.TB,
							"/dev/sde" to 1.TB,
							"/dev/sdf" to 1.TB,
							"/dev/sdg" to 1.TB,
							"/dev/sdh" to 1.TB
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							blockDevices = listOf(
									BlockDevice("/dev/sda", 1.TB),
									BlockDevice("/dev/sdb", 1.TB),
									BlockDevice("/dev/sdc", 1.TB),
									BlockDevice("/dev/sdd", 1.TB),
									BlockDevice("/dev/sde", 1.TB),
									BlockDevice("/dev/sdf", 1.TB),
									BlockDevice("/dev/sdg", 1.TB),
									BlockDevice("/dev/sdh", 1.TB)
							),
							storageCapabilities = listOf(
									lvmStorageCapability
							)
					)
			)
			MirrorVolume(
					host = host,
					vStorage = testDisk,
					capability = lvmStorageCapability,
					mirrors = 6.toUShort(),
					allocation = VirtualStorageLvmAllocation(
							capabilityId = lvmStorageCapability.id,
							mirrors = 0,
							hostId = host.id,
							vgName = lvmStorageCapability.volumeGroupName,
							path = "",
							actualSize = 1.GB
					)
			)
		}
		assertThrows<IllegalStateException>("Not enough devices to mirror") {
			val lvmStorageCapability = LvmStorageCapability(
					size = 8.TB,
					volumeGroupName = "vg-1",
					physicalVolumes = mapOf(
							"/dev/sda" to 1.TB,
							"/dev/sdb" to 1.TB
					)
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
			MirrorVolume(
					host = host,
					vStorage = testDisk,
					capability = lvmStorageCapability,
					mirrors = 2.toUShort(),
					allocation = VirtualStorageLvmAllocation(
							capabilityId = lvmStorageCapability.id,
							mirrors = 0,
							hostId = host.id,
							vgName = lvmStorageCapability.volumeGroupName,
							path = "",
							actualSize = 1.GB
					)
			)
		}

	}


}