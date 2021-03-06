package com.github.kerubistan.kerub.planner.steps.storage.lvm.duplicate

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class DuplicateToLvmFactoryTest : AbstractFactoryVerifications(DuplicateToLvmFactory) {

	private val readOnlyDisk = VirtualStorageDevice(
			id = randomUUID(),
			name = "kakukklinux.iso",
			size = 5.GB,
			readOnly = true
	)

	private val readWriteDisk = VirtualStorageDevice(
			id = randomUUID(),
			name = "database.qcow",
			size = 5.GB,
			readOnly = false
	)

	@Test
	fun produce() {
		assertTrue("There is only one host- no duplicate step") {
			val lvmCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									lvmCapability
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
															vgName = "kerub-test-vg",
															capabilityId = lvmCapability.id
													)
											)
									)
							)
					)
			) == listOf<DuplicateToLvm>()
		}
		assertTrue("two hosts, read-only disk allocated on only one") {
			val lvmCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
					volumeGroupName = "kerub-test-vg"
			)
			val sourceHost = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(lvmCapability)
					)
			)
			val targetCapacity = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
					vgName = "kerub-test-vg",
					capabilityId = lvmCapability.id
			)
			val targetAllocation = VirtualStorageLvmAllocation(
					hostId = targetHost.id,
					path = "/dev/kerub-test-vg/${readOnlyDisk.id}",
					actualSize = 100.GB,
					vgName = "kerub-test-vg",
					capabilityId = targetCapacity.id
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
													CompositeStorageDeviceDynamic(
															id = targetCapacity.id,
															reportedFreeCapacity = targetCapacity.size // all free
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
							virtualStorageDevice = readOnlyDisk
					)
			)
		}

		assertTrue("two hosts, read-only disk allocated on both") {
			val sourceCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
													CompositeStorageDeviceDynamic(
															id = sourceCapability.id,
															reportedFreeCapacity = targetCapability.size // all free
													)
											)
									),
									HostDynamic(
											id = targetHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													CompositeStorageDeviceDynamic(
															id = targetCapability.id,
															reportedFreeCapacity = targetCapability.size // all free
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
															vgName = "kerub-test-vg",
															capabilityId = sourceCapability.id
													),
													VirtualStorageLvmAllocation(
															hostId = targetHost.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg",
															capabilityId = targetCapability.id
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
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
													CompositeStorageDeviceDynamic(
															id = sourceCapability.id,
															reportedFreeCapacity = targetCapability.size // all free
													)
											)
									),
									HostDynamic(
											id = targetHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													CompositeStorageDeviceDynamic(
															id = targetCapability.id,
															reportedFreeCapacity = targetCapability.size // all free
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
															vgName = "kerub-test-vg",
															capabilityId = sourceCapability.id
													))
									)
							)
					)
			).isEmpty()
		}

		assertTrue("two hosts, read-only disk, already has duplicate") {
			val sourceCapability = LvmStorageCapability(
					id = randomUUID(),
					size = 2.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
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
													CompositeStorageDeviceDynamic(
															id = sourceCapability.id,
															reportedFreeCapacity = targetCapability.size // all free
													)
											)
									),
									HostDynamic(
											id = targetHost.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													CompositeStorageDeviceDynamic(
															id = targetCapability.id,
															reportedFreeCapacity = targetCapability.size // all free
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
															vgName = "kerub-test-vg",
															capabilityId = sourceCapability.id
													),
													VirtualStorageLvmAllocation(
															hostId = targetHost.id,
															path = "",
															actualSize = 100.GB,
															vgName = "kerub-test-vg",
															capabilityId = targetCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}

	}
}