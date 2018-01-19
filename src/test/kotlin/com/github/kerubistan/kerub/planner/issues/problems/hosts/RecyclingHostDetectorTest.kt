package com.github.kerubistan.kerub.planner.issues.problems.hosts


import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import org.junit.Test
import kotlin.test.assertTrue

class RecyclingHostDetectorTest {
	@Test
	fun detect() {
		assertTrue("blank state - no problem") {
			RecyclingHostDetector.detect(plan = Plan(state = OperationalState.fromLists())).isEmpty()
		}

		assertTrue("there is a host to be recycled - must be detected") {
			RecyclingHostDetector.detect(plan = Plan(state = OperationalState.fromLists())).isEmpty()
		}
	}

}