package com.github.K0zka.kerub.model

import org.junit.Test

class ExpectationLevelComparatorTest {

	@Test
	fun testCompare() {
		assert(ExpectationLevel.comparator.compare(ExpectationLevel.Wish , ExpectationLevel.Want) < 0 )
		assert(ExpectationLevel.comparator.compare(ExpectationLevel.Wish , ExpectationLevel.DealBreaker) < 0 )
		assert(ExpectationLevel.comparator.compare(ExpectationLevel.Wish , ExpectationLevel.Wish) == 0 )
		assert(ExpectationLevel.comparator.compare(ExpectationLevel.Want , ExpectationLevel.DealBreaker) < 0 )
		assert(ExpectationLevel.comparator.compare(ExpectationLevel.DealBreaker , ExpectationLevel.DealBreaker) == 0 )
	}
}