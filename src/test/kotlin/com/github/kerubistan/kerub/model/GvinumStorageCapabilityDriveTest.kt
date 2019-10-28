package com.github.kerubistan.kerub.model

import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class GvinumStorageCapabilityDriveTest : AbstractDataRepresentationTest<GvinumStorageCapabilityDrive>() {
	override val testInstances = listOf(
			GvinumStorageCapabilityDrive(
					name = "test-drive",
					device = "/dev/sda",
					size = 4.TB
			)
	)
	override val clazz = GvinumStorageCapabilityDrive::class.java

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
