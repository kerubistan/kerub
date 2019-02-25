package com.github.kerubistan.kerub.model

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class GvinumStorageCapabilityDriveTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			GvinumStorageCapabilityDrive(
					size = (-1).toBigInteger(),
					name = "xxx",
					device = "xxx"
			)
		}
	}
}
