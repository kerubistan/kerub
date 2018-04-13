package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.expect
import org.junit.Test

class TimeCostTest {

	@Test
	fun plus() {
		kotlin.test.expect(TimeCost(2000, 2300)) { TimeCost(1000, 1100) + TimeCost(1000, 1200) }
	}

	@Test
	fun init() {
		expect(IllegalArgumentException::class) {
			TimeCost(-1, 1)
		}
		expect(IllegalArgumentException::class) {
			TimeCost(10, 9)
		}
	}
}