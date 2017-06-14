package com.github.K0zka.kerub.model

import com.github.K0zka.kerub.model.expectations.NotSameStorageExpectation
import com.github.K0zka.kerub.testDisk
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