package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.testGvinumCapability
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class HostCapabilitiesTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException>("invalid value for totalMemory") {
			testHostCapabilities.copy(
					totalMemory = (-1).toBigInteger()
			)
		}
		assertThrows<IllegalStateException>("two gvinum capabilities") {
			testHostCapabilities.copy(
					storageCapabilities = listOf(
							testGvinumCapability,
							testGvinumCapability
					)
			)
		}
	}

	@Test
	fun compressionCapabilities() {
		assertEquals(
				setOf(CompressionFormat.Gzip, CompressionFormat.Xz),
				testHostCapabilities.copy(
						installedSoftware = listOf(
								SoftwarePackage(name = "gzip", version = Version.fromVersionString("1.2.3")),
								SoftwarePackage(name = "xz-utils", version = Version.fromVersionString("1.2.3"))
						)
				).compressionCapabilities
		)
	}

}