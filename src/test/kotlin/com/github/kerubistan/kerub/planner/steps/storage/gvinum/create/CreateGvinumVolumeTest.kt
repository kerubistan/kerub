package com.github.kerubistan.kerub.planner.steps.storage.gvinum.create

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.ConcatenatedGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.StripedGvinumConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualDisk
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class CreateGvinumVolumeTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("Capability must be registered in the host") {
			CreateGvinumVolume(
					host = testHost,
					capability = testGvinumCapability,
					config = SimpleGvinumConfiguration(diskName = testGvinumCapability.devices.first().name),
					disk = testDisk
			)
		}
		assertThrows<IllegalStateException>("disk referenced by name not in the gvinum capability") {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(testGvinumCapability)
							)
					),
					capability = testGvinumCapability,
					config = SimpleGvinumConfiguration(diskName = "TEST-intentionally-not-existing-disk-name"),
					disk = testDisk
			)
		}
		assertThrows<IllegalStateException>("disk referenced by name in striped config not in the gvinum capability") {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(testGvinumCapability)
							)
					),
					capability = testGvinumCapability,
					config = StripedGvinumConfiguration(
							disks = listOf(
									testGvinumCapability.devices.first().name, //this is fine
									"TEST-intentionally-not-existing-disk-name" // this won't be found
							)),
					disk = testDisk
			)
		}
		assertThrows<IllegalStateException>("disk referenced by name in concat config not in the gvinum capability") {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(testGvinumCapability)
							)
					),
					capability = testGvinumCapability,
					config = ConcatenatedGvinumConfiguration(
							disks = mapOf(
									testGvinumCapability.devices.first().name to 100.GB, //this is fine
									"TEST-intentionally-not-existing-disk-name" to 100.GB // this won't be found
							)
					),
					disk = testDisk
			)
		}

	}

	@Test
	fun takeWithoutFreeBsd() {
		expect("GVinum is only running on FreeBSD, not on Debian", IllegalArgumentException::class) {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									os = OperatingSystem.Linux,
									distribution = SoftwarePackage("Debian", Version.fromVersionString("8.2")),
									storageCapabilities = listOf(testGvinumCapability)
							)
					),
					disk = testVirtualDisk,
					config = SimpleGvinumConfiguration(
							diskName = "gvinum-disk-1"
					),
					capability = testGvinumCapability
			).take(OperationalState.fromLists())
		}
		expect("GVinum is only running on FreeBSD, not on NetBSD", IllegalArgumentException::class) {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									os = OperatingSystem.BSD,
									distribution = SoftwarePackage("NetBSD", Version.fromVersionString("8.2")),
									storageCapabilities = listOf(testGvinumCapability)
							)
					),
					disk = testVirtualDisk,
					config = SimpleGvinumConfiguration(
							diskName = "gvinum-disk-1"
					),
					capability = testGvinumCapability
			).take(OperationalState.fromLists())
		}
		expect("The gvinum volume creation requires a running host", IllegalArgumentException::class) {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									os = OperatingSystem.BSD,
									distribution = SoftwarePackage("FreeBSD", Version.fromVersionString("9.0")),
									storageCapabilities = listOf(testGvinumCapability)
							)
					),
					disk = testVirtualDisk,
					config = SimpleGvinumConfiguration(
							diskName = "gvinum-disk-1"
					),
					capability = testGvinumCapability
			).take(OperationalState.fromLists())

		}

		assertTrue("A step must create the planned allocation") {
			val gvinumStorageCapability = GvinumStorageCapability(
					devices = listOf(
							GvinumStorageCapabilityDrive(
									size = 1.TB,
									name = "test-disk",
									device = "/dev/sda"
							)
					)
			)
			val host = testFreeBsdHost.copy(
					capabilities = testFreeBsdHost.capabilities!!.copy(
							storageCapabilities = listOf(gvinumStorageCapability)
					)
			)
			val disk = testDisk.copy(
					size = 100.GB

			)
			val state = CreateGvinumVolume(
					host = host,
					disk = disk,
					config = SimpleGvinumConfiguration(diskName = gvinumStorageCapability.devices.first().name),
					capability = gvinumStorageCapability
			).take(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = host.id, status = HostStatus.Up)),
							vStorage = listOf(disk)
					)
			)
			state.vStorage[disk.id]?.dynamic?.allocations == listOf(
					VirtualStorageGvinumAllocation(
							hostId = host.id,
							actualSize = disk.size,
							configuration = SimpleGvinumConfiguration(diskName = gvinumStorageCapability.devices.first().name),
							capabilityId = gvinumStorageCapability.id
					)
			)
		}
	}

	@Test
	fun takeNegativeCases() {
		assertThrows<IllegalArgumentException>("The absurd case when a linux host has a gvinum capability - let's blow it up right here") {
			val gvinumStorageCapability = GvinumStorageCapability(
					devices = listOf(
							GvinumStorageCapabilityDrive(
									size = 1.TB,
									name = "test-disk",
									device = "/dev/sda"
							)
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(gvinumStorageCapability)
					)
			)
			val disk = testDisk.copy(
					size = 100.GB

			)
			val state = CreateGvinumVolume(
					host = host,
					disk = disk,
					config = SimpleGvinumConfiguration(diskName = gvinumStorageCapability.devices.first().name),
					capability = gvinumStorageCapability
			).take(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = host.id, status = HostStatus.Up)),
							vStorage = listOf(disk)
					)
			)
			state.vStorage[disk.id]?.dynamic?.allocations == listOf(
					VirtualStorageGvinumAllocation(
							hostId = host.id,
							actualSize = disk.size,
							configuration = SimpleGvinumConfiguration(diskName = gvinumStorageCapability.devices.first().name),
							capabilityId = gvinumStorageCapability.id
					)
			)
		}
	}
}