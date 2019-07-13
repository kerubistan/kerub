package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector

/**
 * An problem detector checking LVM pools and reporting all over-allocated pools that could fill up and threaten service
 * level.
 */
object LvmPoolAlmostFullDetector : ProblemDetector<LvmPoolAlmostFull> {
	override fun detect(plan: Plan): Collection<LvmPoolAlmostFull> {
		TODO()
	}
}