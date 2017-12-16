package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.create

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.StorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class CreateLvmPoolFactoryTest {
	@Test
	fun produce() {
		assertTrue {
			CreateLvmPoolFactory.produce(
					OperationalState.fromLists()
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
											physicalVolumes = listOf(2.TB, 2.TB),
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
													StorageDeviceDynamic(id = storageId, freeCapacity = 3.TB)
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