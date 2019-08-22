package com.github.kerubistan.kerub.planner.steps.storage.gvinum.create

import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumStorageDeviceDynamicItem
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualDisk
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.MB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class CreateGvinumVolumeFactoryTest : AbstractFactoryVerifications(CreateGvinumVolumeFactory) {

	@Test
	fun produceConcatenatedDisks() {
		val testDisk = testVirtualDisk.copy(
				size = 3.GB
		)
		val gvinum1 = GvinumStorageCapability(
				id = UUID.randomUUID(),
				devices = listOf(
						GvinumStorageCapabilityDrive(name = "ada1", device = "/dev/ada1", size = 2.GB),
						GvinumStorageCapabilityDrive(name = "adb1", device = "/dev/adb1", size = 2.GB)
				)
		)
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
												GvinumStorageDeviceDynamic(
														id = gvinum1.id,
														items = listOf(
																GvinumStorageDeviceDynamicItem(
																		name = "ada1",
																		freeCapacity = 2.GB
																),
																GvinumStorageDeviceDynamicItem(
																		name = "adb1",
																		freeCapacity = 2.GB
																)
														)
												)
										)
								)
						),
						vStorage = listOf(testDisk.copy(
								expectations = listOf(StorageAvailabilityExpectation())
						)),
						vStorageDyns = listOf()
				)
		).isNotEmpty(), "should give concatenated configurations")

	}

	@Test
	fun produce() {
		val gvinum1 = GvinumStorageCapability(
				id = UUID.randomUUID(),
				devices = listOf(
						GvinumStorageCapabilityDrive(
								device = "ada1",
								name = "a",
								size = 20.GB
						)
				)
		)
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
												GvinumStorageDeviceDynamic(
														id = gvinum1.id,
														items = listOf(GvinumStorageDeviceDynamicItem(
																name = "ada1",
																freeCapacity = 20.GB
														))
												)
										)
								)
						),
						vStorage = listOf(testVirtualDisk.copy(
								expectations = listOf(StorageAvailabilityExpectation())
						)),
						vStorageDyns = listOf()
				)
		).isNotEmpty(), "all should be given for an allocation")

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
												GvinumStorageDeviceDynamic(
														id = gvinum1.id,
														items = listOf(
																GvinumStorageDeviceDynamicItem(
																		name = "ada1",
																		freeCapacity = 20.GB
																)
														)
												)
										)
								)
						),
						vStorage = listOf(testVirtualDisk.copy(
								expectations = listOf(StorageAvailabilityExpectation())
						)),
						vStorageDyns = listOf(),
						config = ControllerConfig(
								storageTechnologies = StorageTechnologiesConfig(gvinumCreateVolumeEnabled = false)
						)
				)
		).isEmpty(), "Disabled factory should not generate anything")

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
												GvinumStorageDeviceDynamic(
														id = gvinum1.id,
														items = listOf(
																GvinumStorageDeviceDynamicItem(
																		name = "ada1",
																		freeCapacity = 100.MB
																)
														)
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