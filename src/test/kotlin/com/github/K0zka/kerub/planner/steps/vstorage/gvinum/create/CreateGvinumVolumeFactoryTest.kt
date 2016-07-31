package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.StorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testFreeBsdHost
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testVirtualDisk
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertTrue

class CreateGvinumVolumeFactoryTest {

	@Test
	fun produce() {
		val gvinum1 = GvinumStorageCapability(
				size = "10 GB".toSize(),
				device = "ada1",
				name = "a"
		)
		assertFalse(CreateGvinumVolumeFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(
								testHost,
								testFreeBsdHost.copy(
										capabilities = testFreeBsdHost.capabilities?.copy(
												storageCapabilities = listOf(
														gvinum1
												)
										)
								)
						),
						hostDyns = listOf(
								HostDynamic(
										id = testHost.id,
										status = HostStatus.Up
								),
								HostDynamic(
										id = testFreeBsdHost.id,
										status = HostStatus.Up,
										storageStatus = listOf(
												StorageDeviceDynamic(
														id = gvinum1.id,
														freeCapacity = "20 GB".toSize()
												)
										)
								)
						),
						vStorage = listOf(testVirtualDisk.copy(
								expectations = listOf(StorageAvailabilityExpectation())
						)),
						vStorageDyns = listOf()
				)
		).isEmpty())

		assertTrue(CreateGvinumVolumeFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(
								testHost,
								testFreeBsdHost.copy(
										capabilities = testFreeBsdHost.capabilities?.copy(
												storageCapabilities = listOf(
														gvinum1
												)
										)
								)
						),
						hostDyns = listOf(
								HostDynamic(
										id = testHost.id,
										status = HostStatus.Up
								),
								HostDynamic(
										id = testFreeBsdHost.id,
										status = HostStatus.Up,
										storageStatus = listOf(
												StorageDeviceDynamic(
														id = gvinum1.id,
														freeCapacity = "100 MB".toSize()
												)
										)
								)
						),
						vStorage = listOf(testVirtualDisk.copy(
								expectations = listOf(StorageAvailabilityExpectation())
						)),
						vStorageDyns = listOf()
				)
		).isEmpty(), "100 MB free space is not enough for 1 GB disk allocation")

	}
}