package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector

object PoolAlmostFullDetector : ProblemDetector<PoolAlmostFull> {
	override fun detect(plan: Plan): Collection<PoolAlmostFull> {
		TODO()
	}
}