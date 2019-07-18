package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertTrue

class HostCapabilitiesIndexTest {
	@Test
	fun getCompressionCapabilities() {
		assertTrue("no compression packages, no compression capabilities") {
			testHostCapabilities.copy(
				installedSoftware = listOf(
				)
			).index.compressionCapabilities.isEmpty()
		}
		assertTrue("gzip installed") {
			testHostCapabilities.copy(
					installedSoftware = listOf(
							pack("gzip", "1.2.3")
					)
			).index.compressionCapabilities == setOf(CompressionFormat.Gzip)
		}
	}
}