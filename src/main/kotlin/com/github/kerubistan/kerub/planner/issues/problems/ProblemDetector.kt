package com.github.kerubistan.kerub.planner.issues.problems

import com.github.kerubistan.kerub.planner.Plan

interface ProblemDetector<out T : Problem> {
	fun detect(plan: Plan): Collection<T>
}