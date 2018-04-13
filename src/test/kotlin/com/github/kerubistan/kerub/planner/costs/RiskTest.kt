package com.github.kerubistan.kerub.planner.costs

import org.junit.Test
import kotlin.test.assertTrue

class RiskTest {

	@Test
	fun plus() {
		assertTrue { (Risk(score = 1, comment = "first") + Risk(score = 2, comment = "second")).score == 3 }
	}
}