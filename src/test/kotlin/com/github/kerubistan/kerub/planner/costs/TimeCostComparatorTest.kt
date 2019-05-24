package com.github.kerubistan.kerub.planner.costs

import org.junit.Assert
import org.junit.Test

class TimeCostComparatorTest {

	private val cost1 = TimeCost(minMs = 100, maxMs = 200)
	private val cost2 = TimeCost(minMs = 110, maxMs = 190)
	private val cost3 = TimeCost(minMs = 200, maxMs = 400)

	@Test
	fun compare() {
		Assert.assertEquals(TimeCostComparator.compare(cost1, cost2), 0)
		Assert.assertEquals(TimeCostComparator.compare(cost2, cost1), 0)
		Assert.assertTrue(TimeCostComparator.compare(cost1, cost3) < 0)
		Assert.assertTrue(TimeCostComparator.compare(cost3, cost1) > 0)
	}
}