package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class RemoveLvmPoolFactoryTest : AbstractFactoryVerifications(RemoveLvmPoolFactory) {
	@Test
	fun produce() {
		assertTrue("single blank host should not produce remove lvm steps") {
			RemoveLvmPoolFactory.produce(OperationalState.fromLists(
					hosts = listOf(testHost)
			)).isEmpty()
		}

		assertTrue("single host without lvm vg should not produce remove lvm steps") {
			RemoveLvmPoolFactory.produce(OperationalState.fromLists(
					hosts = listOf(testHost.copy(
							capabilities = testHostCapabilities
					))
			)).isEmpty()
		}

		assertTrue("single host with single unused vg should not produce remove lvm steps") {
			RemoveLvmPoolFactory.produce(OperationalState.fromLists(
					hosts = listOf(testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(
											LvmStorageCapability(
													size = 2.TB,
													physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
													volumeGroupName = "test-vg-1",
													id = UUID.randomUUID()
											)
									)
							)
					))
			)).isEmpty()
		}

		assertTrue("host with single vg and used pool should not produce remove lvm steps") {
			RemoveLvmPoolFactory.produce(OperationalState.fromLists(
					hosts = listOf(testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(
											LvmStorageCapability(
													size = 2.TB,
													physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
													volumeGroupName = "test-vg-1",
													id = UUID.randomUUID()
											)
									)
							)
					)),
					hostCfgs = listOf(
							HostConfiguration(
									id = testHost.id,
									storageConfiguration = listOf(
											LvmPoolConfiguration(
													size = 128.GB,
													vgName = "test-vg-1",
													poolName = "test-pool"
											)
									)
							)
					),
					vStorage = listOf(
							testDisk
					),
					vStorageDyns = listOf(
							VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf(
											VirtualStorageLvmAllocation(
													pool = "test-pool",
													hostId = testHost.id,
													path = "/dev/test-vg-1/${testDisk.id}",
													actualSize = 1.GB,
													vgName = "test-vg-1",
													capabilityId = testLvmCapability.id
											)
									)
							)
					)
			)).isEmpty()
		}

		assertTrue("host with single vg and unused pool should produce a remove lvm step") {
			val steps = RemoveLvmPoolFactory.produce(OperationalState.fromLists(
					hosts = listOf(testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(
											LvmStorageCapability(
													size = 2.TB,
													physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB),
													volumeGroupName = "test-vg-1",
													id = UUID.randomUUID()
											)
									)
							)
					)),
					hostCfgs = listOf(
							HostConfiguration(
									id = testHost.id,
									storageConfiguration = listOf(
											LvmPoolConfiguration(
													size = 128.GB,
													vgName = "test-vg-1",
													poolName = "test-pool"
											)
									)
							)
					)
			))
			steps.single().let {
				it.pool == "test-pool"
						&& it.vgName == "test-vg-1"
			}
		}

	}
}
