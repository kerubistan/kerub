package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.testFsCapability
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class FsStorageCapabilityTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			testFsCapability.copy(size = (-1).toBigInteger())
		}
	}
}