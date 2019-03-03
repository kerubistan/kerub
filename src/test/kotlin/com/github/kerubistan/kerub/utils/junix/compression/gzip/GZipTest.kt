package com.github.kerubistan.kerub.utils.junix.compression.gzip

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GZipTest {

	@Test
	fun getFormat() {
		assertEquals(CompressionFormat.Gzip, GZip.format)
	}

	@Test
	fun available() {
		assertTrue("gzip should be available") {
			GZip.available(
					testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage("gzip", Version.fromVersionString("1.2.3"))
							)
					)
			)
		}
		assertFalse("gzip shouldn't be available") {
			GZip.available(
					testHostCapabilities.copy(
							installedSoftware = listOf()
					)
			)
		}
	}
}