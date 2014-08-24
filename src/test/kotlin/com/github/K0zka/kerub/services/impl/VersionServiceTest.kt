package com.github.K0zka.kerub.services.impl

import org.junit.Test
import org.junit.Assert

public class VersionServiceTest {
	Test
	fun getVersionInfo() {
		Assert.assertNotNull(VersionServiceImpl().getVersionInfo())
	}
}