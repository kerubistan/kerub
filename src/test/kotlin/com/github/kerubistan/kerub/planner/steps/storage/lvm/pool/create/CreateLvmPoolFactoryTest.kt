package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class CreateLvmPoolFactoryTest : AbstractFactoryVerifications(CreateLvmPoolFactory) {
	@Test
	fun produce() {
		assertTrue("when there is no free space on the vg, produce nothing") {
			val storageId = UUID.randomUUID()
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							storageCapabilities = listOf(
									LvmStorageCapability(
											id = storageId,
											size = 4.TB,
											physicalVolumes = mapOf("/dev/sda" to 2.TB, "/dev/sdb" to 2.TB),
											volumeGroupName = "test-vg-1"
									)
							)

					)
			)
			CreateLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = storageId, freeCapacity = 0.TB)
											)

									)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf()
									)
							)
					)
			).isEmpty()

		}

		assertTrue("Actually anything less then 16 GB is considered too small") {
			val storageId = UUID.randomUUID()
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							storageCapabilities = listOf(
									LvmStorageCapability(
											id = storageId,
											size = 4.TB,
											physicalVolumes = mapOf("/dev/sda" to 2.TB, "/dev/sdb" to 2.TB),
											volumeGroupName = "test-vg-1"
									)
							)

					)
			)
			CreateLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = storageId, freeCapacity = 15.GB)
											)
									)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf()
									)
							)
					)
			).isEmpty()

		}


		assertTrue {
			val storageId = UUID.randomUUID()
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							storageCapabilities = listOf(
									LvmStorageCapability(
											id = storageId,
											size = 4.TB,
											physicalVolumes = mapOf("/dev/sda" to 2.TB, "/dev/sdb" to 2.TB),
											volumeGroupName = "test-vg-1"
									)
							)

					)
			)
			CreateLvmPoolFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up,
											storageStatus = listOf(
													SimpleStorageDeviceDynamic(id = storageId, freeCapacity = 3.TB)
											)

									)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											storageConfiguration = listOf(

											)
									)
							)
					)
			).isNotEmpty()

		}
	}

}