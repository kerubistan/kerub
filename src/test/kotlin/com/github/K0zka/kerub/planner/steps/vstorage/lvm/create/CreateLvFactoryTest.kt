package com.github.K0zka.kerub.planner.steps.vstorage.lvm.create

import com.github.K0zka.kerub.model.controller.config.ControllerConfig
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.StorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.io.BusType
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class CreateLvFactoryTest {

	val volumeGroup = LvmStorageCapability(
			id = UUID.randomUUID(),
			size = "1 TB".toSize(),
			physicalVolumes = listOf(),
			volumeGroupName = "testvg"
	)

	val host = Host(
			id = UUID.randomUUID(),
			dedicated = true,
			address = "host-1.example.com",
			capabilities = HostCapabilities(
					os = OperatingSystem.Linux,
					cpuArchitecture = "x86_64",
					totalMemory = "1 GB".toSize(),
					distribution = SoftwarePackage(name = "CentOS", version = Version.fromVersionString("7.0")),
					storageCapabilities = listOf<StorageCapability>(volumeGroup)
			),
			publicKey = ""
	)

	val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			size = "0.5 TB".toSize(),
			name = "test disk"
	)

	val vm = VirtualMachine(
			id = UUID.randomUUID(),
			virtualStorageLinks = listOf(
					VirtualStorageLink(
							virtualStorageId = vDisk.id,
							bus = BusType.sata
					)
			),
			expectations = listOf(VirtualMachineAvailabilityExpectation(up = true)),
			name = "test-vm"
	)

	@Test
	fun produceWithDisabled() {
		val steps = CreateLvFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up,
						storageStatus = listOf(
								StorageDeviceDynamic(
										id = volumeGroup.id,
										freeCapacity = "1 TB".toSize()
								)
						)
				)),
				vms = listOf(vm),
				vStorage = listOf(vDisk),
				vStorageDyns = listOf(),
				config = ControllerConfig(
						storageTechnologies = StorageTechnologiesConfig(lvmCreateVolumeEnabled = false)
				)
		))

		assertTrue(steps.isEmpty())
	}

	@Test
	fun testProduce() {
		val step = CreateLvFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up,
						storageStatus = listOf(
								StorageDeviceDynamic(
										id = volumeGroup.id,
										freeCapacity = "1 TB".toSize()
								)
						)
				)),
				vms = listOf(vm),
				vStorage = listOf(vDisk),
				vStorageDyns = listOf()
		)).single()

		assertEquals(vDisk, step.disk)
		assertEquals(host, step.host)
		assertEquals(volumeGroup.volumeGroupName, step.volumeGroupName)
	}
}