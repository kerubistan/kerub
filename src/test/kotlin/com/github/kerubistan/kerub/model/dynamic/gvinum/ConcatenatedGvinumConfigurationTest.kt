package com.github.kerubistan.kerub.model.dynamic.gvinum

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class ConcatenatedGvinumConfigurationTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			ConcatenatedGvinumConfiguration(disks = mapOf())
		}
	}
}