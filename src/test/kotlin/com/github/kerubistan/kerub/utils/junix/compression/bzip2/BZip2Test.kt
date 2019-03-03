package com.github.kerubistan.kerub.utils.junix.compression.bzip2

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BZip2Test {

	@Test
	fun available() {
		assertFalse("no bzip2") {
			BZip2.available(null)
		}
		assertFalse("no bzip2") {
			BZip2.available(testHostCapabilities.copy(
					installedSoftware = listOf()
			))
		}
		assertTrue("bzip2 installed") {
			BZip2.available(testHostCapabilities.copy(
					installedSoftware = listOf(
							SoftwarePackage(name = "bzip2", version = Version.fromVersionString("1.0.6"))
					)
			))
		}
	}

	@Test
	fun getFormat() {
		assertEquals(CompressionFormat.Bzip2, BZip2.format)
	}
}