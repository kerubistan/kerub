package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.Centos
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
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
													CompositeStorageDeviceDynamic(
															id = storageId,
															reportedFreeCapacity = 0.TB
													)
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
													CompositeStorageDeviceDynamic(
															id = storageId,
															reportedFreeCapacity = 15.GB
													)
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
							distribution = SoftwarePackage.pack(Centos, "7.0"),
							installedSoftware = listOf(
									SoftwarePackage.pack("lvm2","1.2.3"),
									SoftwarePackage.pack("device-mapper-persistent-data","1.2.3")
							),
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
													CompositeStorageDeviceDynamic(
															id = storageId,
															reportedFreeCapacity = 3.TB
													)
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

		assertFalse("") {
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
													CompositeStorageDeviceDynamic(
															id = storageId,
															reportedFreeCapacity = 3.TB
													)
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