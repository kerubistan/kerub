package com.github.kerubistan.kerub.utils.junix.compression.lz4

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Lz4Test {

	@Test
	fun getFormat() {
		assertEquals(CompressionFormat.Lz4, Lz4.format)
	}

	@Test
	fun available() {
		assertFalse("lz4 not available") {
			Lz4.available(null)
		}
		assertFalse("lz4 not available") {
			Lz4.available(testHostCapabilities.copy(installedSoftware = listOf()))
		}
		assertTrue("lz4 available") {
			Lz4.available(
					testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage(
											name = "liblz4-tool",
											version = Version.fromVersionString("0.0")))))
		}
	}
}