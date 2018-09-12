package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.host.distros.Distribution
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class HostCapabilitiesDiscovererImplTest {

	@Test
	fun detectAndBenchmark() {
		assertTrue("just the storage capabilities unchanged if the benchmarks are disabled") {
			val session = mock<ClientSession>()
			val distro = mock<Distribution>()
			val distribution = SoftwarePackage(name = "Centos", version = Version.fromVersionString("7.8"))
			val config = ControllerConfig(
					storageTechnologies = StorageTechnologiesConfig(
							storagebenchmarkingEnabled = false
					)
			)
			val packages = listOf<SoftwarePackage>()

			val capability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 5.GB,
					fsType = "ext4",
					mountPoint = "/kerub")

			whenever(distro.detectStorageCapabilities(eq(session), eq(distribution), eq(packages)))
					.thenReturn(listOf(capability))

			HostCapabilitiesDiscovererImpl
					.detectAndBenchmark(distro, session, distribution, packages, config) == listOf(capability)

		}
		assertTrue("the storage capabilities with benchmark data if the benchmarks are enabled") {
			val session = mock<ClientSession>()
			val distro = mock<Distribution>()
			val distribution = SoftwarePackage(name = "Centos", version = Version.fromVersionString("7.8"))
			val config = ControllerConfig(
					storageTechnologies = StorageTechnologiesConfig(
							storagebenchmarkingEnabled = true
					)
			)
			val packages = listOf<SoftwarePackage>()

			val capability = FsStorageCapability(
					id = UUID.randomUUID(),
					size = 5.GB,
					fsType = "ext4",
					mountPoint = "/kerub")

			whenever(distro.detectStorageCapabilities(eq(session), eq(distribution), eq(packages)))
					.thenReturn(listOf(capability))
			val updatedCapability = capability.copy(performanceInfo = "TEST DONE")
			whenever(distro.storageBenchmark(
					eq(session),
					eq(capability),
					eq(distribution),
					eq(packages),
					eq(config.storageTechnologies))
			).thenReturn(updatedCapability)

			HostCapabilitiesDiscovererImpl
					.detectAndBenchmark(distro, session, distribution, packages, config) == listOf(updatedCapability)

		}
	}

}