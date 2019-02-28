package com.github.kerubistan.kerub.utils.junix.common

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsKtTest {

	@Test
	fun anyPackageNamed() {
		assertFalse("no info, no package") {
			(null as HostCapabilities?).anyPackageNamed("something something")
		}
		assertFalse("no installed software, no package") {
			(testHostCapabilities.copy(installedSoftware = listOf())).anyPackageNamed("something something")
		}
		assertTrue("now it should work") {
			(
					testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage(name = "shalala", version = Version.fromVersionString("1.0"))
							)
					)
					).anyPackageNamed("shalala")
		}
	}
}