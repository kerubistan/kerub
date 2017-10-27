package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.testDisk
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class VirtualStorageDeviceTest {

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