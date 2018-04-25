package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon.StartNfsDaemon
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class PlanComparatorTest {

	@Test
	fun compare() {
		assertTrue("empty plan should be equal to an empty plan") {
			PlanComparator.compare(
					Plan(states = listOf(), steps = listOf()), Plan(states = listOf(), steps = listOf())
			) == 0
		}

		assertTrue("if an empty plan is a solution, it is better than taking any steps") {
			PlanComparator.compare(
					Plan(states = listOf(), steps = listOf()),
					Plan(states = listOf(), steps = listOf(StartNfsDaemon(host = testHost)))
			) < 0
		}

	}
}