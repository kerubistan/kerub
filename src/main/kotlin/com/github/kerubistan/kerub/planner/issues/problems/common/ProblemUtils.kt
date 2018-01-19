package com.github.kerubistan.kerub.planner.issues.problems.common

import com.github.kerubistan.kerub.planner.Plan

fun recyclingHostMap(
		plan: Plan) =
		plan.state.hosts.values.filter { it.stat.recycling }.associateBy { it.stat.id }
