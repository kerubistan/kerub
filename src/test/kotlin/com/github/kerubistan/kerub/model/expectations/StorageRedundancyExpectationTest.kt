package com.github.kerubistan.kerub.model.expectations

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class StorageRedundancyExpectationTest {
	@Test
	fun validate() {
		assertThrows<IllegalStateException>("Number of copies must be greater than zero") {
			StorageRedundancyExpectation(nrOfCopies = 0)
		}
	}
}