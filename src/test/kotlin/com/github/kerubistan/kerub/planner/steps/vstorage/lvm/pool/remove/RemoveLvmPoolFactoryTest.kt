package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.remove

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class RemoveLvmPoolFactoryTest {
	@Test
	fun produce() {
		assertTrue("empty state should not produce remove lvm steps") {
			RemoveLvmPoolFactory.produce(OperationalState.fromLists()).isEmpty()
		}

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
													size = "2 TB".toSize(),
													physicalVolumes = listOf("1 TB".toSize(), "1 TB".toSize()),
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
													size = "2 TB".toSize(),
													physicalVolumes = listOf("1 TB".toSize(), "1 TB".toSize()),
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
													size = "128 GB".toSize(),
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
													actualSize = "1 GB".toSize(),
													vgName = "test-vg-1"
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
													size = "2 TB".toSize(),
													physicalVolumes = listOf("1 TB".toSize(), "1 TB".toSize()),
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
													size = "128 GB".toSize(),
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
