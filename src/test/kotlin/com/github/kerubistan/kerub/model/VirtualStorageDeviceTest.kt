package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.testDisk
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class VirtualStorageDeviceTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			testDisk.copy(size = (-1).toBigInteger())
		}
	}

	@Test
	fun references() {
		assertTrue(testDisk.references().isEmpty())
		assertTrue(testDisk.copy(
				expectations = listOf(
						NotSameStorageExpectation(
								otherDiskId = UUID.randomUUID()
						)
				)
		).references().isNotEmpty())
	}
}