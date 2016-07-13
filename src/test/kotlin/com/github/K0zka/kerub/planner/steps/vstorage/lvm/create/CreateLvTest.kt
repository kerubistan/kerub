package com.github.K0zka.kerub.planner.steps.vstorage.lvm.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.StorageDeviceDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.utils.only
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test

import org.junit.Assert.*
import java.util.UUID

class CreateLvTest {

	val volume = LvmStorageCapability(
			volumeGroupName = "testvg",
			id = UUID.randomUUID(),
			size = "200 GB".toSize(),
			physicalVolumes = listOf()
	)

	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = "",
			capabilities = HostCapabilities(
					storageCapabilities = listOf<StorageCapability>(
							volume
					),
					cpuArchitecture = "",
					totalMemory = "128 GB".toSize(),
					distribution = SoftwarePackage(name = "CentOs", version = Version.fromVersionString("7.0")),
					os = OperatingSystem.Linux
			)
	)

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up,
			storageStatus = listOf(
					StorageDeviceDynamic(
							freeCapacity = "200 GB".toSize(),
							id = volume.id
					)
			)
	)

	val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "test disk",
			size = "100 GB".toSize()
	)

	@Test
	fun take() {
		val transformed = CreateLv(host, "testvg", vDisk).take(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDyn),
						vStorage = listOf(vDisk)
				)
		)

		assertEquals(vDisk.size, transformed.vStorageDyns.values.only().actualSize)
		assertEquals("100 GB".toSize(), transformed.hostDyns.values.only().storageStatus.only().freeCapacity)
	}

	@Test
	fun getCost() {
		assertTrue(CreateLv(host, "testvg", vDisk).getCost().isNotEmpty())
	}

	@Test
	fun reservations() {

	}
}