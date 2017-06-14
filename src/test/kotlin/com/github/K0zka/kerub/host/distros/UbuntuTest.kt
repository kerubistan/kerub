package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UbuntuTest {

	@Test
	fun testHandlesVersion() {
		assertFalse(Ubuntu().handlesVersion(Version.fromVersionString("blah")))
		assertFalse(Ubuntu().handlesVersion(Version.fromVersionString("blah.blah")))
		assertFalse(Ubuntu().handlesVersion(Version.fromVersionString("10.04")))
		assertTrue(Ubuntu().handlesVersion(Version.fromVersionString("12.04")))
		assertTrue(Ubuntu().handlesVersion(Version.fromVersionString("14.04")))
	}
}