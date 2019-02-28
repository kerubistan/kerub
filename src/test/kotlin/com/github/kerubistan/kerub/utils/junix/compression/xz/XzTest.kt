package com.github.kerubistan.kerub.utils.junix.compression.xz

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class XzTest {

	@Test
	fun available() {
		assertFalse("xz not available") {
			Xz.available(null)
		}
		assertFalse("xz not available") {
			Xz.available(testHostCapabilities.copy(installedSoftware = listOf()))
		}
		assertTrue("xz is available") {
			Xz.available(
					testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage(name = "xz-utils", version = Version.fromVersionString("5.2.2"))
							)))
		}
	}
}