package com.github.kerubistan.kerub.planner.costs

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class TimeCostTest {

	@Test
	fun plus() {
		kotlin.test.expect(TimeCost(2000, 2300)) { TimeCost(1000, 1100) + TimeCost(1000, 1200) }
	}

	@Test
	fun init() {
		assertThrows<IllegalArgumentException> {
			TimeCost(-1, 1)
		}
		assertThrows<IllegalArgumentException> {
			TimeCost(10, 9)
		}
	}
}