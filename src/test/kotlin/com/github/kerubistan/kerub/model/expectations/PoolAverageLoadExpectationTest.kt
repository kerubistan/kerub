package com.github.kerubistan.kerub.model.expectations

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class PoolAverageLoadExpectationTest {
	@Test
	fun validations() {
		//maximum less than minimum
		assertThrows<IllegalStateException> {
			PoolAverageLoadExpectation(max = 50, min = 60, toleranceMs = 10000)
		}
		//negative minimum
		assertThrows<IllegalStateException> {
			PoolAverageLoadExpectation(max = 50, min = -10, toleranceMs = 10000)
		}
		//maximum higher than 100
		assertThrows<IllegalStateException> {
			PoolAverageLoadExpectation(max = 110, min = 10, toleranceMs = 10000)
		}
		//negative tolerance
		assertThrows<IllegalStateException> {
			PoolAverageLoadExpectation(max = 90, min = 10, toleranceMs = -10000)
		}
	}
}