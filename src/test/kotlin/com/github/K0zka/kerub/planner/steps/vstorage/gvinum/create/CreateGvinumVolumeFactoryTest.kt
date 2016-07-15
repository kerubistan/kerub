package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testFreeBsdHost
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testVirtualDisk
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert.assertFalse
import org.junit.Test

class CreateGvinumVolumeFactoryTest {

	@Test
	fun produce() {
		val steps = CreateGvinumVolumeFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(
								testHost,
								testFreeBsdHost.copy(
										capabilities = testFreeBsdHost.capabilities?.copy(
												storageCapabilities = listOf(
														GvinumStorageCapability(
																size = "10 GB".toSize(),
																device = "ada1",
																name = "a"
														)
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
										status = HostStatus.Up
								)
						),
						vStorage = listOf(testVirtualDisk.copy(
								expectations = listOf(StorageAvailabilityExpectation())
						)),
						vStorageDyns = listOf()
				)
		)
		assertFalse(steps.isEmpty())
	}
}