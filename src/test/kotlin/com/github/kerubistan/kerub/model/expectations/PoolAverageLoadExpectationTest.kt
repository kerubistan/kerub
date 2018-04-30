package com.github.kerubistan.kerub.model.expectations

import com.github.kerubistan.kerub.expect
import org.junit.Test

class PoolAverageLoadExpectationTest {
	@Test
	fun validations() {
		//maximum less than minimum
		expect(IllegalStateException::class) {
			PoolAverageLoadExpectation(max = 50, min = 60, toleranceMs = 10000)
		}
		//negative minimum
		expect(IllegalStateException::class) {
			PoolAverageLoadExpectation(max = 50, min = -10, toleranceMs = 10000)
		}
		//maximum higher than 100
		expect(IllegalStateException::class) {
			PoolAverageLoadExpectation(max = 110, min = 10, toleranceMs = 10000)
		}
		//negative tolerance
		expect(IllegalStateException::class) {
			PoolAverageLoadExpectation(max = 90, min = 10, toleranceMs = -10000)
		}
	}
}