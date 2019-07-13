package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertTrue

@Ignore
class LvmPoolAlmostFullDetectorTest : AbstractProblemDetectorVerifications(LvmPoolAlmostFullDetector) {

	@Test
	fun detect() {
		assertTrue("lvm pool over-allocated, 99 percent full, could") {
			TODO()
		}
		assertTrue("99% full but no overallocation at all, it fits nicely - no problem") {
			LvmPoolAlmostFullDetector.detect(
					Plan(
							OperationalState.fromLists(
									hosts = listOf(
											testHost.copy()
									)
							)
					)
			).isEmpty()
		}
		TODO()
	}
}