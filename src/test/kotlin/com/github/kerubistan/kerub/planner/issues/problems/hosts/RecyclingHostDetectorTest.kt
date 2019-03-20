package com.github.kerubistan.kerub.planner.issues.problems.hosts


import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import org.junit.Test
import kotlin.test.assertTrue

class RecyclingHostDetectorTest : AbstractProblemDetectorVerifications(RecyclingHostDetector) {
	@Test
	fun detect() {
		assertTrue("there is a host to be recycled - must be detected") {
			RecyclingHostDetector.detect(plan = Plan(state = OperationalState.fromLists())).isEmpty()
		}
	}

}