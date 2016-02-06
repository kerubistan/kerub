package com.github.K0zka.kerub.services.impl

import org.junit.Assert
import org.junit.Test

class VersionServiceTest {
	@Test
	fun getVersionInfo() {
		Assert.assertNotNull(VersionServiceImpl().getVersionInfo())
	}
	@Test
	fun logStart() {
		VersionServiceImpl().logStart()
	}
	@Test
	fun logStop() {
		VersionServiceImpl().logStop()
	}
}