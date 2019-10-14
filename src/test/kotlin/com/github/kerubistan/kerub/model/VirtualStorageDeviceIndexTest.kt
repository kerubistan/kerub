package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.testDisk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

internal class VirtualStorageDeviceIndexTest {

	@Test
	fun getExpectationsByClass() {
		assertTrue(
				testDisk.copy(
						expectations = listOf(
								StorageAvailabilityExpectation(),
								NotSameStorageExpectation(otherDiskId = randomUUID()),
								NotSameStorageExpectation(otherDiskId = randomUUID())
						)
				).index.expectationsByClass.let { groups ->
					groups.containsKey(StorageAvailabilityExpectation::class.qualifiedName)
							&& groups.containsKey(NotSameStorageExpectation::class.qualifiedName)
				}
		)
	}
}