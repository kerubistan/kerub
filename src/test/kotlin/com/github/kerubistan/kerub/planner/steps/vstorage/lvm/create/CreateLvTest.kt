package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse

class CreateLvTest {

	val volume = LvmStorageCapability(
			volumeGroupName = "testvg",
			id = UUID.randomUUID(),
			size = 200.GB,
			physicalVolumes = mapOf("/dev/sda" to 200.GB)
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
					totalMemory = 128.GB,
					distribution = SoftwarePackage(name = "CentOs", version = Version.fromVersionString("7.0")),
					os = OperatingSystem.Linux
			)
	)

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up,
			storageStatus = listOf(
					SimpleStorageDeviceDynamic(
							freeCapacity = 200.GB,
							id = volume.id
					)
			)
	)

	private val vDisk = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "test disk",
			size = 100.GB
	)

	@Test
	fun take() {
		val transformed = CreateLv(host, volume, vDisk).take(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDyn),
						vStorage = listOf(vDisk)
				)
		)

		assertEquals(vDisk.size, transformed.vStorage.values.single().dynamic?.allocations?.single()?.actualSize)
		assertEquals(100.GB, transformed.hosts.values.single().dynamic?.storageStatus?.single()?.freeCapacity)
	}

	@Test
	fun getCost() {
		assertTrue(CreateLv(host, volume, vDisk).getCost().isNotEmpty())
	}

	@Test
	fun reservations() {
		assertFalse(CreateLv(host, volume, vDisk).reservations().isEmpty())
	}
}