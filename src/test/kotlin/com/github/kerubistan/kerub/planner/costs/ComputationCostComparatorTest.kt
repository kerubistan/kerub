package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.model.Host
import org.junit.Assert
import org.junit.Test

class ComputationCostComparatorTest {
	val host = Host(
			address = "host-1.example.com",
	        dedicated = false,
	        publicKey = ""
	               )
	private val smaller = ComputationCost(
			host = host,
	        cycles = 100
	                             )
	private val bigger = ComputationCost(
			host = host,
			cycles = 1000
	                             )

	@Test
	fun compare() {
		Assert.assertTrue(ComputationCostComparator.compare(smaller, bigger) < 0)
		Assert.assertTrue(ComputationCostComparator.compare(bigger, smaller) > 0)
		Assert.assertEquals(ComputationCostComparator.compare(bigger, bigger), 0)
	}
}