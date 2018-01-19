package com.github.kerubistan.kerub.planner.issues.problems

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import org.junit.Test
import kotlin.test.assertTrue

class CompositeProblemDetectorImplTest {
	@Test
	fun detect() {
		assertTrue("an empty state must be problem-free") {
			CompositeProblemDetectorImpl.detect(plan = Plan(state = OperationalState.fromLists())).isEmpty()
		}
	}

}