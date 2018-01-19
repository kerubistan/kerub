package com.github.kerubistan.kerub.planner.issues.problems.hosts

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector

object RecyclingHostDetector : ProblemDetector<RecyclingHost> {
	override fun detect(plan: Plan): Collection<RecyclingHost>
			= plan.state.hosts.values.filter { it.stat.recycling }.map { RecyclingHost(host = it.stat) }

}