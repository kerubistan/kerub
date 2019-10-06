package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.junix.common.Centos
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import kotlin.test.assertTrue

class CreateThinLvFactoryTest : AbstractFactoryVerifications(CreateThinLvFactory) {
	@Test
	fun produce() {
		assertTrue("blank state should not produce any step - regardless of lvm enabled") {
			CreateThinLvFactory.produce(
					OperationalState.fromLists(
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(lvmCreateVolumeEnabled = true)))
			).isEmpty()
		}

		assertTrue("blank state should not produce any step - regardless of lvm disabled") {
			CreateThinLvFactory.produce(
					OperationalState.fromLists(
							config = ControllerConfig(
									storageTechnologies = StorageTechnologiesConfig(lvmCreateVolumeEnabled = false)))
			).isEmpty()
		}

		val baseHostWithoutLvmDependencies = testHost.copy(
				capabilities = testHostCapabilities.copy(
						os = OperatingSystem.Linux,
						distribution = SoftwarePackage.pack(Centos, "7.0")
				)
		)

		val baseHostWithLvmDependencies = baseHostWithoutLvmDependencies.copy(
				capabilities = baseHostWithoutLvmDependencies.capabilities!!.copy(
						installedSoftware = listOf(
								SoftwarePackage.pack("lvm2", "1.2.3"),
								SoftwarePackage.pack("device-mapper-persistent-data", "1.2.3")
						)
				)
		)

		assertTrue("host does not have a pool, no steps produced") {
			val disk = testDisk.copy(size = 16.TB)

			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = disk.id,
									bus = BusType.sata
							)
					),
					expectations = listOf(VirtualMachineAvailabilityExpectation())
			)
			val host = baseHostWithLvmDependencies.copy(
					capabilities = baseHostWithLvmDependencies.capabilities!!.copy(
							storageCapabilities = listOf()
					)
			)
			val hostConfig = HostConfiguration(
					storageConfiguration = listOf(
							//no pool
					)
			)
			CreateThinLvFactory.produce(
					OperationalState.fromLists(
							vms = listOf(vm),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									)
							),
							hostCfgs = listOf(
									hostConfig
							)
					)
			).isEmpty()
		}

		assertTrue("host have a pool - step should be generated") {
			val disk = testDisk.copy(
					size = 16.TB,
					expectations = listOf(StorageAvailabilityExpectation())
			)

			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									virtualStorageId = disk.id,
									bus = BusType.sata
							)
					),
					expectations = listOf(VirtualMachineAvailabilityExpectation())
			)
			val storageCapability = LvmStorageCapability(
					size = 2.TB,
					volumeGroupName = "test-vg",
					physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB)
			)
			val host = baseHostWithLvmDependencies.copy(
					capabilities = baseHostWithLvmDependencies.capabilities!!.copy(
							storageCapabilities = listOf(
									storageCapability
							)
					)
			)
			val hostConfig = HostConfiguration(
					id = host.id,
					storageConfiguration = listOf(
							LvmPoolConfiguration(
									size = 1.TB,
									poolName = "test-pool",
									vgName = "test-vg"
							)
					)
			)
			val steps = CreateThinLvFactory.produce(
					OperationalState.fromLists(
							vms = listOf(vm),
							vStorage = listOf(disk),
							hosts = listOf(host),
							hostDyns = listOf(
									HostDynamic(
											id = host.id,
											status = HostStatus.Up
									)
							),
							hostCfgs = listOf(
									hostConfig
							)
					)
			)
			steps == listOf(
					CreateThinLv(
							host = host,
							poolName = "test-pool",
							disk = disk,
							capability = storageCapability
					)
			)
		}

	}

}