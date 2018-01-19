package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.Constrained
import com.github.kerubistan.kerub.model.Expectation

interface PlanViolationDetector {
	fun listViolations(plan : Plan) : Map<Constrained<out Expectation>, List<Expectation>>
}