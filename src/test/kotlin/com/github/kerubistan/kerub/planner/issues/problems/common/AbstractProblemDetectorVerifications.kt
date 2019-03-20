package com.github.kerubistan.kerub.planner.issues.problems.common

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import org.junit.Test
import kotlin.test.assertTrue

abstract class AbstractProblemDetectorVerifications(private val problemDetector: ProblemDetector<*>) {
	@Test
	fun checkBlankState() {
		assertTrue("blank state should not have any problem") {
			problemDetector.detect(Plan(state = OperationalState.fromLists())).isEmpty()
		}
	}
}