package com.github.kerubistan.kerub.model.expectations

import com.github.kerubistan.kerub.expect
import org.junit.Test

class PoolRunningVmsExpectationTest {
	@Test
	fun validation() {
		// negative minimum vms
		expect(IllegalStateException::class) {
			PoolRunningVmsExpectation(min = -1)
		}
		// max less than min
		expect(IllegalStateException::class) {
			PoolRunningVmsExpectation(min = 4, max = 2)
		}
	}

}