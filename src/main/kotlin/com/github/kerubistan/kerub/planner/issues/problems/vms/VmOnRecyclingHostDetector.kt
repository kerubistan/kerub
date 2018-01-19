package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.issues.problems.common.recyclingHostMap

object VmOnRecyclingHostDetector : ProblemDetector<VmOnRecyclingHost> {
	override fun detect(plan: Plan): Collection<VmOnRecyclingHost> {
		val recyclingHosts = recyclingHostMap(plan)
		return if (recyclingHosts.isEmpty()) {
			listOf()
		} else {
			plan.state.vms.values.filter { it.dynamic?.hostId in recyclingHosts }.map {
				VmOnRecyclingHost(vm = it.stat, host = recyclingHosts[it.dynamic!!.hostId]!!.stat)
			}
		}
	}
}