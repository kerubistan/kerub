package com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.create

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
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualDisk
import org.junit.Test
import kotlin.test.assertTrue

class CreateGvinumVolumeTest {

	@Test
	fun takeWithoutFreeBsd() {
		expect("GVinum is onnly running on FreeBSD, not on Debian", IllegalArgumentException::class) {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									os = OperatingSystem.Linux,
									distribution = SoftwarePackage("Debian", Version.fromVersionString("8.2"))
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
									distribution = SoftwarePackage("NetBSD", Version.fromVersionString("8.2"))
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
									distribution = SoftwarePackage("FreeBSD", Version.fromVersionString("9.0"))
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
}