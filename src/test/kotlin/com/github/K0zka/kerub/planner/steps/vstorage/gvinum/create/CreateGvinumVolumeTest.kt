package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.testVirtualDisk
import org.junit.Test
import java.util.UUID

class CreateGvinumVolumeTest {

	@Test
	fun takeWithoutFreeBsd() {
		expect(IllegalArgumentException::class) {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									os = OperatingSystem.Linux,
									distribution = SoftwarePackage("Debian", Version.fromVersionString("8.2"))
							)
					),
					disk = testVirtualDisk,
					config = SimpleGvinumConfiguration(
							diskId = UUID.randomUUID()
					)
			).take(OperationalState.fromLists())
		}
		expect(IllegalArgumentException::class) {
			CreateGvinumVolume(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									os = OperatingSystem.BSD,
									distribution = SoftwarePackage("NetBSD", Version.fromVersionString("8.2"))
							)
					),
					disk = testVirtualDisk,
					config = SimpleGvinumConfiguration(
							diskId = UUID.randomUUID()
					)
			).take(OperationalState.fromLists())
		}
	}
}