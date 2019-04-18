package com.github.kerubistan.kerub.model.expectations

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class PoolRunningVmsExpectationTest {
	@Test
	fun validation() {
		// negative minimum vms
		assertThrows<IllegalStateException> {
			PoolRunningVmsExpectation(min = -1)
		}
		// max less than min
		assertThrows<IllegalStateException> {
			PoolRunningVmsExpectation(min = 4, max = 2)
		}
	}

}