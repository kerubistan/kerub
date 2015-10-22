package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.Host
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

public class ComputationCostComparatorTest {
	val host = Host(
			address = "host-1.example.com",
	        dedicated = false,
	        publicKey = ""
	               )
	val smaller = ComputationCost(
			host = host,
	        cycles = 100
	                             )
	val bigger = ComputationCost(
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