package com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.create

import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualDisk
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