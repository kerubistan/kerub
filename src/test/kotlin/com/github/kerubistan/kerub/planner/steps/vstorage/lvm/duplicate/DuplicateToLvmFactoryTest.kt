package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.duplicate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.StorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testOtherHost
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

internal class DuplicateToLvmFactoryTest {

	val readOnlyDisk = VirtualStorageDevice(
			id = randomUUID(),
			name = "kakukklinux.iso",
			size = 5.GB,
			readOnly = true
	)

	val readWriteDisk = VirtualStorageDevice(
			id = randomUUID(),
			name = "database.qcow",
			size = 5.GB,
			readOnly = false
	)

	@Test
	fun produce() {
		assertTrue("blank state should produce no steps") {
			DuplicateToLvmFactory.produce(OperationalState.fromLists()) == listOf<DuplicateToLvm>()
		}
		assertTrue("There is only one host- no duplicate step") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									LvmStorageCapability(
											id = randomUUID(),
											size = 2.TB,
											physicalVolumes = listOf(1.TB, 1.TB),
											volumeGroupName = "kerub-test-vg"
									)
							)
					)
			)
			DuplicateToLvmFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									)
							),
							vStorage = listOf(readOnlyDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = readOnlyDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = host.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg"
													)
											)
									)
							)
					)
			) == listOf<DuplicateToLvm>()
		}
		assertTrue("two hosts, read-only disk allocated on only one") {
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									LvmStorageCapability(
											id = randomUUID(),
											size = 2.TB,
											physicalVolumes = listOf(1.TB, 1.TB),
											volumeGroupName = "kerub-test-vg"
									)
							)
					)
			)
			val targetCapacity = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = listOf(1.TB, 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									targetCapacity
							)
					)
			)
			val sourceAllocation = VirtualStorageLvmAllocation(
					hostId = sourceHost.id,
					path = "",
					actualSize = 100.GB,
					vgName = "kerub-test-vg"
			)
			val targetAllocation = VirtualStorageLvmAllocation(
					hostId = targetHost.id,
					path = "/dev/kerub-test-vg/${readOnlyDisk.id}",
					actualSize = 100.GB,
					vgName = "kerub-test-vg"
			)
			DuplicateToLvmFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targetHost),
							hostDyns = listOf(
									HostDynamic(
											id = sourceHost.id,
											status = HostStatus.Up
									),
									HostDynamic(
											id = targetHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													StorageDeviceDynamic(
															id = targetCapacity.id,
															freeCapacity = targetCapacity.size // all free
													)
											)
									)

							),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "SOURCE-HOST-PUBLIC-KEY"
									),
									HostConfiguration(
											id = targetHost.id,
											acceptedPublicKeys = listOf("SOURCE-HOST-PUBLIC-KEY")
									)
							),
							vStorage = listOf(readOnlyDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = readOnlyDisk.id,
											allocations = listOf(
													sourceAllocation
											)
									)
							)
					)
			) == listOf(
					DuplicateToLvm(
							source = sourceAllocation,
							sourceHost = sourceHost,
							target = targetAllocation,
							targetHost = targetHost,
							vStorageDevice = readOnlyDisk
					)
			)
		}

		assertTrue("two hosts, read-only disk allocated on both") {
			val sourceCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = listOf(1.TB, 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									sourceCapability
							)
					)
			)
			val targetCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = listOf(1.TB, 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									targetCapability
							)
					)
			)
			DuplicateToLvmFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targetHost),
							hostDyns = listOf(
									HostDynamic(
											id = sourceHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													StorageDeviceDynamic(
															id = sourceCapability.id,
															freeCapacity = targetCapability.size // all free
													)
											)
									),
									HostDynamic(
											id = targetHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													StorageDeviceDynamic(
															id = targetCapability.id,
															freeCapacity = targetCapability.size // all free
													)
											)
									)

							),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "SOURCE-HOST-PUBLIC-KEY"
									),
									HostConfiguration(
											id = targetHost.id,
											acceptedPublicKeys = listOf("SOURCE-HOST-PUBLIC-KEY")
									)
							),
							vStorage = listOf(readOnlyDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = readOnlyDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = sourceHost.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg"
													),
													VirtualStorageLvmAllocation(
															hostId = targetHost.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg"
													)

											)
									)
							)
					)
			).isEmpty()
		}


		assertTrue("two hosts, read-write disk") {
			val sourceCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = listOf(1.TB, 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									sourceCapability
							)
					)
			)
			val targetCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = listOf(1.TB, 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val targetHost = testOtherHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									targetCapability
							)
					)
			)
			DuplicateToLvmFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(sourceHost, targetHost),
							hostDyns = listOf(
									HostDynamic(
											id = sourceHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													StorageDeviceDynamic(
															id = sourceCapability.id,
															freeCapacity = targetCapability.size // all free
													)
											)
									),
									HostDynamic(
											id = targetHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													StorageDeviceDynamic(
															id = targetCapability.id,
															freeCapacity = targetCapability.size // all free
													)
											)
									)

							),
							hostCfgs = listOf(
									HostConfiguration(
											id = sourceHost.id,
											publicKey = "SOURCE-HOST-PUBLIC-KEY"
									),
									HostConfiguration(
											id = targetHost.id,
											acceptedPublicKeys = listOf("SOURCE-HOST-PUBLIC-KEY")
									)
							),
							vStorage = listOf(readWriteDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = readWriteDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = sourceHost.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg"
													),
													VirtualStorageLvmAllocation(
															hostId = targetHost.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg"
													)

											)
									)
							)
					)
			).isEmpty()
		}

	}
}