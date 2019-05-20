package com.github.kerubistan.kerub.model

import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GvinumStorageCapabilityTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException>("No disk, no gvinum storage capability") {
			GvinumStorageCapability(devices = listOf())
		}
	}

	@Test
	fun size() {
		assertEquals(
				3.TB,
				GvinumStorageCapability(
						devices = listOf(
								GvinumStorageCapabilityDrive(name = "disk-1", device = "sda", size = 1.TB),
								GvinumStorageCapabilityDrive(name = "disk-2", device = "sdb", size = 2.TB)
						)).size
		)
	}
}